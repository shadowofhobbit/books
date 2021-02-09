-- liquibase formatted sql

-- changeset julia:1612025669150-1
CREATE TABLE "public"."accounts" ("id" INTEGER NOT NULL, "password_hash" VARCHAR NOT NULL, "username" VARCHAR NOT NULL, "role" VARCHAR NOT NULL, "email" VARCHAR NOT NULL, "description" TEXT, "confirmed_email" BOOLEAN, "birthday" date, CONSTRAINT "accounts_pkey" PRIMARY KEY ("id"));

-- changeset julia:1612025669150-2
CREATE TABLE "public"."books" ("id" INTEGER NOT NULL, "author" VARCHAR, "title" VARCHAR, "language" VARCHAR, "year" INTEGER, "description" TEXT, CONSTRAINT "book_pkey" PRIMARY KEY ("id"));

-- changeset julia:1612025669150-3
ALTER TABLE "public"."accounts" ADD CONSTRAINT "accounts_email_key" UNIQUE ("email");

-- changeset julia:1612025669150-4
ALTER TABLE "public"."accounts" ADD CONSTRAINT "accounts_username_key" UNIQUE ("username");

-- changeset julia:1612025669150-5
ALTER TABLE "public"."books" ADD CONSTRAINT "author_title" UNIQUE ("author", "title");

-- changeset julia:1612025669150-6
CREATE INDEX "titleloweridx" ON "public"."books"(lower((title)::text));

-- changeset julia:1612025669150-7
CREATE SEQUENCE  IF NOT EXISTS "public"."hibernate_sequence" AS bigint START WITH 1 INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;

-- changeset julia:8
CREATE TABLE reviews (id BIGINT NOT NULL PRIMARY KEY, content TEXT, date TIMESTAMP,
  rating INTEGER NOT NULL CHECK (rating>=1 AND rating<=10), title VARCHAR(255), book_id BIGINT REFERENCES books(id),
  reviewer_id INTEGER REFERENCES accounts(id));
