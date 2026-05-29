import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

serve(async (req: Request) => {
  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!
  );

  const url = new URL(req.url);
  const bookName = url.searchParams.get("bookName");
  const author = url.searchParams.get("author");
  const categoryId = url.searchParams.get("categoryId");
  const status = url.searchParams.get("status");

  let query = supabase.from("books").select("*");

  if (bookName) {
    query = query.ilike("book_name", `%${bookName}%`);
  }
  if (author) {
    query = query.ilike("author", `%${author}%`);
  }
  if (categoryId) {
    query = query.eq("category_id", parseInt(categoryId));
  }
  if (status) {
    query = query.eq("status", parseInt(status));
  }

  const { data: books, error } = await query.order("created_at", { ascending: false });

  if (error) {
    return new Response(
      JSON.stringify({ code: 500, msg: "查询失败", error: error.message }),
      { headers: { "Content-Type": "application/json" } }
    );
  }

  return new Response(
    JSON.stringify({ code: 200, msg: "查询成功", data: books }),
    { headers: { "Content-Type": "application/json" } }
  );
});