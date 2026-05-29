import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

interface LoginRequest {
  username: string;
  password: string;
}

interface UserInfo {
  id: number;
  username: string;
  real_name: string;
  email: string;
  phone: string;
  avatar: string;
  roles: { role_code: string; role_name: string }[];
}

serve(async (req: Request) => {
  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!
  );

  const { username, password }: LoginRequest = await req.json();

  const { data: user, error } = await supabase
    .from("users")
    .select("id, username, password, real_name, email, phone, avatar")
    .eq("username", username)
    .eq("status", 1)
    .single();

  if (error || !user) {
    return new Response(
      JSON.stringify({ code: 401, msg: "用户名或密码错误" }),
      { headers: { "Content-Type": "application/json" } }
    );
  }

  const passwordMatch = await supabase.rpc("verify_password", {
    password: password,
    hash: user.password,
  });

  if (!passwordMatch.data) {
    return new Response(
      JSON.stringify({ code: 401, msg: "用户名或密码错误" }),
      { headers: { "Content-Type": "application/json" } }
    );
  }

  const { data: userRoles, error: rolesError } = await supabase
    .from("user_role")
    .select("role_id")
    .eq("user_id", user.id);

  if (rolesError) {
    return new Response(
      JSON.stringify({ code: 500, msg: "获取角色失败" }),
      { headers: { "Content-Type": "application/json" } }
    );
  }

  const roleIds = userRoles.map((ur: { role_id: number }) => ur.role_id);
  const { data: roles, error: roleError } = await supabase
    .from("roles")
    .select("role_code, role_name")
    .in("id", roleIds);

  const userInfo: UserInfo = {
    id: user.id,
    username: user.username,
    real_name: user.real_name,
    email: user.email,
    phone: user.phone,
    avatar: user.avatar,
    roles: roles.map((r: { role_code: string; role_name: string }) => ({
      role_code: r.role_code,
      role_name: r.role_name,
    })),
  };

  const { data: session, error: sessionError } = await supabase.auth.admin.createUser({
    email: user.email || `${user.username}@example.com`,
    password: password,
    user_metadata: { ...userInfo },
  });

  if (sessionError) {
    return new Response(
      JSON.stringify({ code: 500, msg: "创建会话失败" }),
      { headers: { "Content-Type": "application/json" } }
    );
  }

  return new Response(
    JSON.stringify({ code: 200, msg: "登录成功", data: userInfo }),
    { headers: { "Content-Type": "application/json" } }
  );
});