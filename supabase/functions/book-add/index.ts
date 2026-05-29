import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

interface BookAddRequest {
  isbn?: string;
  book_name: string;
  author?: string;
  publisher?: string;
  publish_date?: string;
  category_id?: number;
  cover_url?: string;
  description?: string;
  total_count: number;
  price?: number;
  location?: string;
}

serve(async (req: Request) => {
  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!
  );

  const body: BookAddRequest = await req.json();

  if (!body.book_name || !body.total_count) {
    return new Response(
      JSON.stringify({ code: 400, msg: "图书名称和数量不能为空" }),
      { headers: { "Content-Type": "application/json" } }
    );
  }

  if (body.isbn) {
    const { data: existingBook } = await supabase
      .from("books")
      .select("id")
      .eq("isbn", body.isbn)
      .single();
    if (existingBook) {
      return new Response(
        JSON.stringify({ code: 400, msg: "ISBN已存在" }),
        { headers: { "Content-Type": "application/json" } }
      );
    }
  }

  const { data: newBook, error } = await supabase
    .from("books")
    .insert({
      isbn: body.isbn,
      book_name: body.book_name,
      author: body.author,
      publisher: body.publisher,
      publish_date: body.publish_date,
      category_id: body.category_id,
      cover_url: body.cover_url,
      description: body.description,
      total_count: body.total_count,
      available_count: body.total_count,
      price: body.price,
      location: body.location,
      status: 1,
    })
    .select()
    .single();

  if (error) {
    return new Response(
      JSON.stringify({ code: 500, msg: "添加失败", error: error.message }),
      { headers: { "Content-Type": "application/json" } }
    );
  }

  return new Response(
    JSON.stringify({ code: 200, msg: "添加成功", data: newBook }),
    { headers: { "Content-Type": "application/json" } }
  );
});