create table if not exists chat (
    id bigint primary key generated always as identity unique,
    title varchar(255) not null,
    created_at timestamp default current_timestamp
);

create table if not exists chat_entry (
    id bigint primary key generated always as identity unique,
    content text,
    role varchar(50),
    chat_id bigint not null references chat(id) on delete cascade,
    created_at timestamp default current_timestamp
);

create table if not exists loaded_document (
    id bigint primary key generated always as identity unique,
    filename varchar(255),
    content_hash varchar(64) not null,
    document_type varchar(10) not null,
    chunk_count integer,
    loaded_at timestamp default current_timestamp,

    constraint unique_document unique (filename, content_hash)
);

create table if not exists vector_store (
    id varchar(255) primary key,
    content text,
    metadata json,
    embedding vector(1024)
);

create index if not exists idx_loaded_documents_filename
    on loaded_document(filename);

create index if not exists vector_store_hnsw_index
    on vector_store using hnsw (embedding vector_cosine_ops);