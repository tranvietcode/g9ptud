CREATE TABLE Account (
 id INTEGER PRIMARY KEY AUTOINCREMENT,
 username TEXT NOT NULL UNIQUE COLLATE NOCASE,
 password TEXT NOT NULL,
 role TEXT NOT NULL CHECK(role IN ('Admin','Student'))
);
-- statement
CREATE TABLE Student (
 id INTEGER PRIMARY KEY AUTOINCREMENT,
 studentCode TEXT NOT NULL UNIQUE COLLATE NOCASE,
 fullname TEXT NOT NULL,
 email TEXT NOT NULL,
 phone TEXT NOT NULL,
 accountId INTEGER NOT NULL UNIQUE REFERENCES Account(id) ON DELETE CASCADE
);
-- statement
CREATE TABLE Subject (
 id INTEGER PRIMARY KEY AUTOINCREMENT,
 subjectCode TEXT NOT NULL UNIQUE COLLATE NOCASE,
 subjectName TEXT NOT NULL,
 description TEXT NOT NULL DEFAULT '',
 credits INTEGER NOT NULL CHECK(credits > 0 AND credits <= 30)
);
-- statement
CREATE TABLE StudySchedule (
 id INTEGER PRIMARY KEY AUTOINCREMENT,
 studentId INTEGER NOT NULL REFERENCES Student(id) ON DELETE CASCADE,
 subjectId INTEGER NOT NULL REFERENCES Subject(id) ON DELETE RESTRICT,
 studyDate TEXT NOT NULL,
 startTime TEXT NOT NULL,
 endTime TEXT NOT NULL CHECK(endTime > startTime),
 location TEXT NOT NULL CHECK(length(trim(location)) > 0),
 status TEXT NOT NULL CHECK(status IN ('Pending','Confirmed','Cancelled'))
);
-- statement
CREATE INDEX idx_schedule_student_date ON StudySchedule(studentId,studyDate);
-- statement
CREATE TRIGGER prevent_overlap_insert BEFORE INSERT ON StudySchedule
WHEN NEW.status != 'Cancelled'
BEGIN
 SELECT RAISE(ABORT, 'SCHEDULE_CONFLICT') WHERE EXISTS (
  SELECT 1 FROM StudySchedule WHERE studentId=NEW.studentId AND studyDate=NEW.studyDate
  AND status != 'Cancelled' AND startTime < NEW.endTime AND endTime > NEW.startTime
 );
END;
-- statement
CREATE TRIGGER prevent_overlap_update BEFORE UPDATE ON StudySchedule
WHEN NEW.status != 'Cancelled'
BEGIN
 SELECT RAISE(ABORT, 'SCHEDULE_CONFLICT') WHERE EXISTS (
  SELECT 1 FROM StudySchedule WHERE id != NEW.id AND studentId=NEW.studentId AND studyDate=NEW.studyDate
  AND status != 'Cancelled' AND startTime < NEW.endTime AND endTime > NEW.startTime
 );
END;
