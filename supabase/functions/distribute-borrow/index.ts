import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

interface BorrowRequest {
  book_id: number;
  user_id: number;
  operator_id?: number;
  due_date?: string;
  remark?: string;
}

serve(async (req: Request) => {
  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!
  );

  const { book_id, user_id, operator_id, due_date, remark }: BorrowRequest = await req.json();

  if (!book_id || !user_id) {
    return new Response(
      JSON.stringify({ code: 400, msg: "图书ID和用户ID不能为空" }),
      { headers: { "Content-Type": "application/json" } }
    );
  }

  const { data: book, error: bookError } = await supabase
    .from("books")
    .select("id, book_name, available_count, status")
    .eq("id", book_id)
    .single();

  if (bookError || !book) {
    return new Response(
      JSON.stringify({ code: 404, msg: "图书不存在" }),
      { headers: { "Content-Type": "application/json" } }
    );
  }

  if (book.available_count <= 0) {
    return new Response(
      JSON.stringify({ code: 400, msg: "图书库存不足" }),
      { headers: { "Content-Type": "application/json" } }
    );
  }

  if (book.status !== 1) {
    return new Response(
      JSON.stringify({ code: 400, msg: "图书已下架" }),
      { headers: { "Content-Type": "application/json" } }
    );
  }

  const defaultDueDate = new Date();
  defaultDueDate.setDate(defaultDueDate.getDate() + 30);

  const { data: record, error: recordError } = await supabase
    .from("distribute_record")
    .insert({
      book_id,
      user_id,
      operator_id,
      borrow_date: new Date().toISOString(),
      due_date: due_date || defaultDueDate.toISOString(),
      status: "BORROWING",
      remark,
      renew_count: 0,
      fine: 0,
    })
    .select()
    .single();

  if (recordError) {
    return new Response(
      JSON.stringify({ code: 500, msg: "借阅失败", error: recordError.message }),
      { headers: { "Content-Type": "application/json" } }
    );
  }

  await supabase
    .from("books")
    .update({ available_count: book.available_count - 1 })
    .eq("id", book_id);

  return new Response(
    JSON.stringify({ code: 200, msg: "借阅成功", data: record }),
    { headers: { "Content-Type": "application/json" } }
  );
});