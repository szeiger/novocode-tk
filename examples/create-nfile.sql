DROP TABLE nfile;

CREATE TABLE nfile (
  name VARCHAR(127) NOT NULL,
  parent VARCHAR(127) NOT NULL,
  content_type VARCHAR(50),
  content_encoding VARCHAR(30),
  mtime BIGINT,
  body BLOB,
  PRIMARY KEY (name),
  KEY (parent)
);

INSERT INTO nfile VALUES ("/", "", NULL, NULL, 0, NULL);
INSERT INTO nfile VALUES ("/foo", "/", NULL, NULL, 0, NULL);
INSERT INTO nfile VALUES ("/bar", "/", NULL, NULL, 0, NULL);
INSERT INTO nfile VALUES ("/bar/baz", "/bar", NULL, NULL, 0, NULL);
INSERT INTO nfile VALUES ("/1", "/", "text/plain", NULL, 0,
  "This is plaintext file /1");
INSERT INTO nfile VALUES ("/2", "/", "text/html", NULL, 0,
  "This is <I>HTML</I> file /2");
INSERT INTO nfile VALUES ("/bar/baz/3", "/bar/baz", "text/plain", NULL, 0,
  "This is plaintext file /bar/baz/3");
INSERT INTO nfile VALUES ("/bar/baz/4", "/bar/baz", "text/plain", NULL, 0,
  "This is plaintext file /bar/baz/4");
INSERT INTO nfile VALUES ("/bar/baz/5", "/bar/baz", "text/plain", NULL, 0,
  "This is plaintext file /bar/baz/5");
