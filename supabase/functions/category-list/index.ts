import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

serve(async (req: Request) => {
  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!
  );

  const { data: categories, error } = await supabase
    .from("category")
    .select("*")
    .eq("status", 1)
    .order("sort_order", { ascending: true });

  if (error) {
    return new Response(
      JSON.stringify({ code: 500, msg: "查询失败", error: error.message }),
      { headers: { "Content-Type": "application/json" } }
    );
  }

  return new Response(
    JSON.stringify({ code: 200, msg: "查询成功", data: categories }),
    { headers: { "Content-Type": "application/json" } }
  );
});