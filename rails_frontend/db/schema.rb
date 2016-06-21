# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20160620191915) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"

  create_table "auth_group", force: :cascade do |t|
    t.string "name", limit: 80, null: false
  end

  add_index "auth_group", ["name"], name: "auth_group_name_a6ea08ec_like", using: :btree
  add_index "auth_group", ["name"], name: "auth_group_name_key", unique: true, using: :btree

  create_table "auth_group_permissions", force: :cascade do |t|
    t.integer "group_id",      null: false
    t.integer "permission_id", null: false
  end

  add_index "auth_group_permissions", ["group_id", "permission_id"], name: "auth_group_permissions_group_id_0cd325b0_uniq", unique: true, using: :btree
  add_index "auth_group_permissions", ["group_id"], name: "auth_group_permissions_0e939a4f", using: :btree
  add_index "auth_group_permissions", ["permission_id"], name: "auth_group_permissions_8373b171", using: :btree

  create_table "auth_permission", force: :cascade do |t|
    t.string  "name",            limit: 255, null: false
    t.integer "content_type_id",             null: false
    t.string  "codename",        limit: 100, null: false
  end

  add_index "auth_permission", ["content_type_id", "codename"], name: "auth_permission_content_type_id_01ab375a_uniq", unique: true, using: :btree
  add_index "auth_permission", ["content_type_id"], name: "auth_permission_417f1b1c", using: :btree

  create_table "auth_user", force: :cascade do |t|
    t.string   "password",     limit: 128, null: false
    t.datetime "last_login"
    t.boolean  "is_superuser",             null: false
    t.string   "username",     limit: 30,  null: false
    t.string   "first_name",   limit: 30,  null: false
    t.string   "last_name",    limit: 30,  null: false
    t.string   "email",        limit: 254, null: false
    t.boolean  "is_staff",                 null: false
    t.boolean  "is_active",                null: false
    t.datetime "date_joined",              null: false
  end

  add_index "auth_user", ["username"], name: "auth_user_username_6821ab7c_like", using: :btree
  add_index "auth_user", ["username"], name: "auth_user_username_key", unique: true, using: :btree

  create_table "auth_user_groups", force: :cascade do |t|
    t.integer "user_id",  null: false
    t.integer "group_id", null: false
  end

  add_index "auth_user_groups", ["group_id"], name: "auth_user_groups_0e939a4f", using: :btree
  add_index "auth_user_groups", ["user_id", "group_id"], name: "auth_user_groups_user_id_94350c0c_uniq", unique: true, using: :btree
  add_index "auth_user_groups", ["user_id"], name: "auth_user_groups_e8701ad4", using: :btree

  create_table "auth_user_user_permissions", force: :cascade do |t|
    t.integer "user_id",       null: false
    t.integer "permission_id", null: false
  end

  add_index "auth_user_user_permissions", ["permission_id"], name: "auth_user_user_permissions_8373b171", using: :btree
  add_index "auth_user_user_permissions", ["user_id", "permission_id"], name: "auth_user_user_permissions_user_id_14a6b632_uniq", unique: true, using: :btree
  add_index "auth_user_user_permissions", ["user_id"], name: "auth_user_user_permissions_e8701ad4", using: :btree

  create_table "django_admin_log", force: :cascade do |t|
    t.datetime "action_time",                 null: false
    t.text     "object_id"
    t.string   "object_repr",     limit: 200, null: false
    t.integer  "action_flag",     limit: 2,   null: false
    t.text     "change_message",              null: false
    t.integer  "content_type_id"
    t.integer  "user_id",                     null: false
  end

  add_index "django_admin_log", ["content_type_id"], name: "django_admin_log_417f1b1c", using: :btree
  add_index "django_admin_log", ["user_id"], name: "django_admin_log_e8701ad4", using: :btree

  create_table "django_content_type", force: :cascade do |t|
    t.string "app_label", limit: 100, null: false
    t.string "model",     limit: 100, null: false
  end

  add_index "django_content_type", ["app_label", "model"], name: "django_content_type_app_label_76bd3d3b_uniq", unique: true, using: :btree

  create_table "django_migrations", force: :cascade do |t|
    t.string   "app",     limit: 255, null: false
    t.string   "name",    limit: 255, null: false
    t.datetime "applied",             null: false
  end

  create_table "django_session", primary_key: "session_key", force: :cascade do |t|
    t.text     "session_data", null: false
    t.datetime "expire_date",  null: false
  end

  add_index "django_session", ["expire_date"], name: "django_session_de54fa62", using: :btree
  add_index "django_session", ["session_key"], name: "django_session_session_key_c0390e0f_like", using: :btree

  create_table "grants_grant", force: :cascade do |t|
    t.string   "organization", limit: 500, null: false
    t.jsonb    "data",                     null: false
    t.datetime "created_at",               null: false
    t.datetime "updated_at"
  end

  create_table "pg_search_documents", force: :cascade do |t|
    t.text     "content"
    t.integer  "searchable_id"
    t.string   "searchable_type"
    t.datetime "created_at",      null: false
    t.datetime "updated_at",      null: false
  end

  add_index "pg_search_documents", ["searchable_type", "searchable_id"], name: "index_pg_search_documents_on_searchable_type_and_searchable_id", using: :btree

  create_table "users", force: :cascade do |t|
    t.string   "name"
    t.string   "email"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
  end

  add_foreign_key "auth_group_permissions", "auth_group", column: "group_id", name: "auth_group_permissions_group_id_b120cbf9_fk_auth_group_id"
  add_foreign_key "auth_group_permissions", "auth_permission", column: "permission_id", name: "auth_group_permiss_permission_id_84c5c92e_fk_auth_permission_id"
  add_foreign_key "auth_permission", "django_content_type", column: "content_type_id", name: "auth_permiss_content_type_id_2f476e4b_fk_django_content_type_id"
  add_foreign_key "auth_user_groups", "auth_group", column: "group_id", name: "auth_user_groups_group_id_97559544_fk_auth_group_id"
  add_foreign_key "auth_user_groups", "auth_user", column: "user_id", name: "auth_user_groups_user_id_6a12ed8b_fk_auth_user_id"
  add_foreign_key "auth_user_user_permissions", "auth_permission", column: "permission_id", name: "auth_user_user_per_permission_id_1fbb5f2c_fk_auth_permission_id"
  add_foreign_key "auth_user_user_permissions", "auth_user", column: "user_id", name: "auth_user_user_permissions_user_id_a95ead1b_fk_auth_user_id"
  add_foreign_key "django_admin_log", "auth_user", column: "user_id", name: "django_admin_log_user_id_c564eba6_fk_auth_user_id"
  add_foreign_key "django_admin_log", "django_content_type", column: "content_type_id", name: "django_admin_content_type_id_c4bce8eb_fk_django_content_type_id"
end
