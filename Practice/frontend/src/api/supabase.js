import { createClient } from '@supabase/supabase-js'
import { ElMessage } from 'element-plus'
import router from '../router'

const supabase = createClient(
  'https://nxjqylsklmxzgbtlcorl.supabase.co',
  'sb_publishable_BhMzmZ4eqq26_aT7W44m9g_M0Lo9BUr'
)

async function handleError(error) {
  if (error) {
    ElMessage.error(error.message || '操作失败')
    return null
  }
  return null
}

export const authAPI = {
  async login(data) {
    const { data: user, error } = await supabase
      .from('users')
      .select('id, username, password, real_name, email, phone, avatar')
      .eq('username', data.username)
      .eq('status', 1)
      .single()

    if (error || !user) {
      ElMessage.error('用户名或密码错误')
      return null
    }

    const { data: verifyResult } = await supabase.rpc('verify_password', {
      password: data.password,
      hash: user.password
    })

    if (!verifyResult) {
      ElMessage.error('用户名或密码错误')
      return null
    }

    const { data: userRoles } = await supabase
      .from('user_role')
      .select('role_id')
      .eq('user_id', user.id)

    const roleIds = userRoles.map(ur => ur.role_id)
    const { data: roles } = await supabase
      .from('roles')
      .select('role_code, role_name')
      .in('id', roleIds)

    const userData = { ...user }
    delete userData.password
    return {
      ...userData,
      roles: roles.map(r => ({ role_code: r.role_code, role_name: r.role_name }))
    }
  },

  async register(data) {
    const { data: existing } = await supabase
      .from('users')
      .select('id')
      .eq('username', data.username)
      .single()

    if (existing) {
      ElMessage.error('用户名已存在')
      return null
    }

    const { data: hashResult } = await supabase.rpc('hash_password', { password: data.password })

    const { data: newUser } = await supabase
      .from('users')
      .insert({
        username: data.username,
        password: hashResult,
        real_name: data.real_name,
        email: data.email,
        phone: data.phone,
        status: 1,
        borrow_limit: 5
      })
      .select('id')
      .single()

    if (!newUser) {
      ElMessage.error('注册失败')
      return null
    }

    const { data: defaultRole } = await supabase
      .from('roles')
      .select('id')
      .eq('role_code', 'USER')
      .single()

    if (defaultRole) {
      await supabase.from('user_role').insert({
        user_id: newUser.id,
        role_id: defaultRole.id
      })
    }

    ElMessage.success('注册成功')
    return newUser
  }
}

export const bookAPI = {
  async list(params = {}) {
    let query = supabase.from('books').select('*')

    if (params.bookName) {
      query = query.ilike('book_name', `%${params.bookName}%`)
    }
    if (params.author) {
      query = query.ilike('author', `%${params.author}%`)
    }
    if (params.categoryId) {
      query = query.eq('category_id', params.categoryId)
    }
    if (params.status !== undefined) {
      query = query.eq('status', params.status)
    }

    const { data, error } = await query.order('created_at', { ascending: false })
    if (error) {
      handleError(error)
      return []
    }
    return data
  },

  async getById(id) {
    const { data, error } = await supabase
      .from('books')
      .select('*')
      .eq('id', id)
      .single()
    if (error) {
      handleError(error)
      return null
    }
    return data
  },

  async add(data) {
    const { data: existing } = await supabase
      .from('books')
      .select('id')
      .eq('isbn', data.isbn)
      .single()

    if (existing) {
      ElMessage.error('ISBN已存在')
      return null
    }

    const { data: newBook, error } = await supabase
      .from('books')
      .insert({
        isbn: data.isbn,
        book_name: data.book_name,
        author: data.author,
        publisher: data.publisher,
        publish_date: data.publish_date,
        category_id: data.category_id,
        cover_url: data.cover_url,
        description: data.description,
        total_count: data.total_count,
        available_count: data.total_count,
        price: data.price,
        location: data.location,
        status: 1
      })
      .select()
      .single()

    if (error) {
      handleError(error)
      return null
    }

    ElMessage.success('添加成功')
    return newBook
  },

  async update(data) {
    const { data: updated, error } = await supabase
      .from('books')
      .update({
        isbn: data.isbn,
        book_name: data.book_name,
        author: data.author,
        publisher: data.publisher,
        publish_date: data.publish_date,
        category_id: data.category_id,
        cover_url: data.cover_url,
        description: data.description,
        total_count: data.total_count,
        price: data.price,
        location: data.location,
        status: data.status
      })
      .eq('id', data.id)
      .select()
      .single()

    if (error) {
      handleError(error)
      return null
    }

    ElMessage.success('更新成功')
    return updated
  },

  async delete(id) {
    const { error } = await supabase.from('books').delete().eq('id', id)
    if (error) {
      handleError(error)
      return false
    }
    ElMessage.success('删除成功')
    return true
  }
}

export const categoryAPI = {
  async list() {
    const { data, error } = await supabase
      .from('category')
      .select('*')
      .eq('status', 1)
      .order('sort_order', { ascending: true })
    if (error) {
      handleError(error)
      return []
    }
    return data
  },

  async add(data) {
    const { data: newCat, error } = await supabase
      .from('category')
      .insert({
        category_name: data.category_name,
        parent_id: data.parent_id || 0,
        sort_order: data.sort_order || 0,
        description: data.description,
        status: 1
      })
      .select()
      .single()

    if (error) {
      handleError(error)
      return null
    }

    ElMessage.success('添加成功')
    return newCat
  },

  async update(data) {
    const { data: updated, error } = await supabase
      .from('category')
      .update({
        category_name: data.category_name,
        parent_id: data.parent_id,
        sort_order: data.sort_order,
        description: data.description,
        status: data.status
      })
      .eq('id', data.id)
      .select()
      .single()

    if (error) {
      handleError(error)
      return null
    }

    ElMessage.success('更新成功')
    return updated
  },

  async delete(id) {
    const { error } = await supabase.from('category').delete().eq('id', id)
    if (error) {
      handleError(error)
      return false
    }
    ElMessage.success('删除成功')
    return true
  }
}

export const distributeAPI = {
  async list(params = {}) {
    let query = supabase.from('distribute_record').select(`
      id,
      book_id,
      user_id,
      operator_id,
      borrow_date,
      due_date,
      return_date,
      status,
      remark,
      renew_count,
      fine,
      created_at,
      books(id, book_name, isbn)
    `)

    if (params.userId) {
      query = query.eq('user_id', params.userId)
    }
    if (params.status) {
      query = query.eq('status', params.status)
    }

    const { data, error } = await query.order('borrow_date', { ascending: false })
    if (error) {
      handleError(error)
      return []
    }

    const userIds = [...new Set(data.map(r => r.user_id).filter(Boolean))]
    const operatorIds = [...new Set(data.map(r => r.operator_id).filter(Boolean))]
    const allIds = [...new Set([...userIds, ...operatorIds])]
    
    let userMap = {}
    if (allIds.length > 0) {
      const { data: users } = await supabase
        .from('users')
        .select('id, username, real_name')
        .in('id', allIds)
      userMap = users.reduce((acc, u) => ({ ...acc, [u.id]: u }), {})
    }

    return data.map(record => ({
      ...record,
      user: userMap[record.user_id] || null,
      operator: userMap[record.operator_id] || null
    }))
  },

  async borrow(data) {
    const { data: book } = await supabase
      .from('books')
      .select('id, available_count, status')
      .eq('id', data.book_id)
      .single()

    if (!book || book.available_count <= 0) {
      ElMessage.error('图书库存不足')
      return null
    }

    const dueDate = new Date()
    dueDate.setDate(dueDate.getDate() + 30)

    const { data: record, error } = await supabase
      .from('distribute_record')
      .insert({
        book_id: data.book_id,
        user_id: data.user_id,
        operator_id: data.operator_id,
        due_date: dueDate.toISOString(),
        status: 'BORROWING',
        remark: data.remark,
        renew_count: 0,
        fine: 0
      })
      .select()
      .single()

    if (error) {
      handleError(error)
      return null
    }

    await supabase
      .from('books')
      .update({ available_count: book.available_count - 1 })
      .eq('id', data.book_id)

    ElMessage.success('借阅成功')
    return record
  },

  async returnBook(id, remark) {
    const { data: record } = await supabase
      .from('distribute_record')
      .select('book_id')
      .eq('id', id)
      .single()

    if (!record) {
      ElMessage.error('记录不存在')
      return null
    }

    const { data: updated, error } = await supabase
      .from('distribute_record')
      .update({
        status: 'RETURNED',
        return_date: new Date().toISOString(),
        remark: remark
      })
      .eq('id', id)
      .select()
      .single()

    if (error) {
      handleError(error)
      return null
    }

    const { data: book } = await supabase
      .from('books')
      .select('available_count')
      .eq('id', record.book_id)
      .single()

    await supabase
      .from('books')
      .update({ available_count: book.available_count + 1 })
      .eq('id', record.book_id)

    ElMessage.success('归还成功')
    return updated
  },

  async myBorrow(userId) {
    const { data, error } = await supabase
      .from('distribute_record')
      .select(`
        id,
        book_id,
        user_id,
        borrow_date,
        due_date,
        status,
        books(id, book_name, isbn)
      `)
      .eq('user_id', userId)
      .eq('status', 'BORROWING')
      .order('borrow_date', { ascending: false })

    if (error) {
      handleError(error)
      return []
    }
    return data
  }
}

export const statisticsAPI = {
  async borrowRate() {
    const { data: totalBooks } = await supabase
      .from('books')
      .select('total_count')
      .eq('status', 1)

    const { data: borrowed } = await supabase
      .from('distribute_record')
      .select('id')
      .eq('status', 'BORROWING')

    const total = totalBooks.reduce((sum, b) => sum + (b.total_count || 0), 0)
    const borrowedCount = borrowed.length

    return { total, borrowed: borrowedCount, rate: total > 0 ? ((borrowedCount / total) * 100).toFixed(2) : 0 }
  },

  async categoryStats() {
    const { data, error } = await supabase
      .from('category')
      .select(`
        id,
        category_name,
        books!inner(id)
      `)
      .eq('status', 1)

    if (error) {
      handleError(error)
      return []
    }

    const stats = {}
    data.forEach(cat => {
      stats[cat.category_name] = stats[cat.category_name] || 0
      stats[cat.category_name]++
    })

    return Object.entries(stats).map(([name, count]) => ({ name, count }))
  },

  async monthlyBorrow() {
    const { data, error } = await supabase
      .from('distribute_record')
      .select('borrow_date')

    if (error) {
      handleError(error)
      return []
    }

    const monthly = {}
    data.forEach(record => {
      const month = record.borrow_date.substring(0, 7)
      monthly[month] = monthly[month] || 0
      monthly[month]++
    })

    return Object.entries(monthly).map(([month, count]) => ({ month, count }))
  }
}

export const userAPI = {
  async list() {
    const { data, error } = await supabase
      .from('users')
      .select('id, username, real_name, email, phone, status, created_at')
      .order('created_at', { ascending: false })

    if (error) {
      handleError(error)
      return []
    }
    return data
  },

  async update(data) {
    const { data: updated, error } = await supabase
      .from('users')
      .update({
        real_name: data.real_name,
        email: data.email,
        phone: data.phone,
        status: data.status
      })
      .eq('id', data.id)
      .select()
      .single()

    if (error) {
      handleError(error)
      return null
    }

    ElMessage.success('更新成功')
    return updated
  },

  async delete(id) {
    const { error } = await supabase.from('users').delete().eq('id', id)
    if (error) {
      handleError(error)
      return false
    }
    ElMessage.success('删除成功')
    return true
  }
}

export const roleAPI = {
  async list() {
    const { data, error } = await supabase
      .from('roles')
      .select('*')
      .order('id', { ascending: true })

    if (error) {
      handleError(error)
      return []
    }
    return data
  }
}

export default supabase