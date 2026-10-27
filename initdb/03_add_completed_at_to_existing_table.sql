SET @completed_at_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'todos'
      AND COLUMN_NAME = 'completed_at'
);
SET @completed_at_sql = IF(
    @completed_at_exists = 0,
    'ALTER TABLE todos ADD COLUMN completed_at DATETIME NULL',
    'SELECT 1'
);
PREPARE completed_at_statement FROM @completed_at_sql;
EXECUTE completed_at_statement;
DEALLOCATE PREPARE completed_at_statement;
