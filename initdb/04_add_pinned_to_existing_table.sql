SET @pinned_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'todos'
      AND COLUMN_NAME = 'pinned'
);
SET @pinned_sql = IF(
    @pinned_exists = 0,
    'ALTER TABLE todos ADD COLUMN pinned BOOLEAN NOT NULL DEFAULT FALSE',
    'SELECT 1'
);
PREPARE pinned_statement FROM @pinned_sql;
EXECUTE pinned_statement;
DEALLOCATE PREPARE pinned_statement;
