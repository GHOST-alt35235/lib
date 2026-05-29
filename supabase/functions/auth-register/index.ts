import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

interface RegisterRequest {
  username: string;
  password: string;
  real_name: string;
  email?: string;
  phone?: string;
}

serve(async (req: Request) => {
  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!
  );

  const { username, password, real_name, email, phone }: RegisterRequest = await req.json();

  if (!username || !password || !real_name) {
    return new Response(
      JSON.stringify({ code: 400, msg: "用户名、密码和真实姓名不能为空" }),
      { headers: { "Content-Type": "application/json" } }
    );
  }

  const { data: existingUser, error: checkError } = await supabase
    .from("users")
    .select("id")
    .eq("username", username)
    .single();

  if (existingUser) {
    return new Response(
      JSON.stringify({ code: 400, msg: "用户名已存在" }),
      { headers: { "Content-Type": "application/json" } }
    );
  }

  const { data: hashResult, error: hashError } = await supabase.rpc("hash_password", {
    password: password,
  });

  if (hashError) {
    return new Response(
      JSON.stringify({ code: 500, msg: "密码加密失败" }),
      { headers: { "Content-Type": "application/json" } }
    );
  }

  const { data: newUser, error: insertError } = await supabase
    .from("users")
    .insert({
      username,
      password: hashResult,
      real_name,
      email,
      phone,
      status: 1,
      borrow_limit: 5,
    })
    .select("id")
    .single();

  if (insertError) {
    return new Response(
      JSON.stringify({ code: 500, msg: "注册失败" }),
      { headers: { "Content-Type": "application/json" } }
    );
  }

  const { data: defaultRole, error: roleError } = await supabase
    .from("roles")
    .select("id")
    .eq("role_code", "USER")
    .single();

  if (!defaultRole) {
    return new Response(
      JSON.stringify({ code: 500, msg: "获取默认角色失败" }),
      { headers: { "Content-Type": "application/json" } }
    );
  }

  await supabase.from("user_role").insert({
    user_id: newUser.id,
    role_id: defaultRole.id,
  });

  return new Response(
    JSON.stringify({ code: 200, msg: "注册成功" }),
    { headers: { "Content-Type": "application/json" } }
  );
});