CREATE TABLE account (
	account_id TEXT PRIMARY KEY,
	name TEXT,
	email TEXT,
	document TEXT,
	password TEXT
);

CREATE TABLE account_asset (
	account_id TEXT,
	asset_id TEXT,
	quantity REAL,
	PRIMARY KEY (account_id, asset_id)
);

CREATE TABLE order_tb (
	order_id TEXT PRIMARY KEY,
	market_id TEXT,
	account_id TEXT,
	side TEXT,
	quantity REAL,
	price REAL,
	fill_quantity REAL,
	fill_price REAL,
	status TEXT,
	timestamp TEXT
);