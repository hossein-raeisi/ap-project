package back.services;

import back.Config;
import back.database.DataBase;
import back.models.Attachment;
import back.models.course.*;
import back.models.faculty.Faculty;
import back.models.messenger.ChatFeed;
import back.models.messenger.Message;
import back.models.request.Request;
import back.models.security.Captcha;
import back.models.users.Professor;
import back.models.users.Student;
import back.models.users.User;
import back.server.Server;
import commons.data_class.*;
import commons.enums.AssignmentAcceptableTypes;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

import static spark.Spark.halt;

public class RawDataHandler {

    public static User getUserFromRequest(spark.Request request) {
        var authToken = String.valueOf(request.headers("auth-token"));
        var userId = UUID.fromString(String.valueOf(request.headers("user-id")));

        if (!Server.activeAuthTokenToUserId.get(authToken).equals(userId))
            halt(400);

        return DataBase.entityManager.find(User.class, userId);
    }

    public static UserData getUserData(User user) {
        return new UserData(
                user.getId(),
                user.getFaculty().getName(),
                user.getFaculty().getId(),
                user.getNationalId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                getAttachmentData(user.getImage())
        );
    }

    public static ProfessorData getProfessorData(Professor professor) {
        return new ProfessorData(
                professor.getId(),
                professor.getFaculty().getName(),
                professor.getFaculty().getId(),
                professor.getNationalId(),
                professor.getFirstName(),
                professor.getLastName(),
                professor.getEmail(),
                professor.getPhoneNumber(),
                getAttachmentData(professor.getImage()),
                professor.getRoomNumber(),
                professor.getProfessorNumber(),
                professor.getType(),
                professor.getRank()
        );
    }

    public static CourseData getCourseData(Course course) {
        return new CourseData(
                course.getId(),
                course.getFaculty().getName(),
                course.getFaculty().getId(),
                course.getProfessor().getFullName(),
                course.getProfessor().getId(),
                course.getLevel(),
                course.getName(),
                course.getSize(),
                course.getTime(),
                course.isTemporaryScoresSet(),
                course.isArchived(),
                new ArrayList<>(course.getExams().stream().map(Exam::getId).collect(Collectors.toList())),
                new ArrayList<>(course.getScores().stream().map(Score::getId).collect(Collectors.toList())),
                new ArrayList<>(course.getWeekDays()),
                new ArrayList<>(course.getTAs().stream().map(User::getId).collect(Collectors.toList())),
                new ArrayList<>(DataBase.getEducationalContents(course).stream().map(EducationalContent::getId).collect(Collectors.toList()))
        );
    }

    public static StudentData getStudentData(Student student) {
        return new StudentData(
                student.getId(),
                student.getFaculty().getName(),
                student.getFaculty().getId(),
                student.getNationalId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getPhoneNumber(),
                getAttachmentData(student.getImage()),
                student.getSupervisorProfessor().getFullName(),
                student.getSupervisorProfessor().getId(),
                student.getEducationalStatus(),
                student.getType(),
                new ArrayList<>(student.getActiveScores().stream().map(Score::getId).collect(Collectors.toList())),
                new ArrayList<>(student.getPassedScores().stream().map(Score::getId).collect(Collectors.toList())),
                Float.parseFloat(student.getTotalGradePointAverage().toString()),
                student.getStaringYear(),
                student.getMajor(),
                student.getStudentNumber()
        );
    }

    public static ExamData getExamData(Exam exam) {
        return new ExamData(
                exam.getId(),
                exam.getCourse().getName(),
                exam.getCourse().getId(),
                exam.getTime(),
                exam.getName()
        );
    }

    public static FacultyData getFacultyData(Faculty faculty) {
        return new FacultyData(
                faculty.getId(),
                new ArrayList<>(faculty.getProfessors().stream().map(Professor::getId).collect(Collectors.toList())),
                faculty.getDeputyEducationProfessor().getFullName(),
                faculty.getDeputyEducationProfessor().getId(),
                faculty.getFacultyHeadProfessor().getFullName(),
                faculty.getFacultyHeadProfessor().getId(),
                faculty.getName(),
                new ArrayList<>(faculty.getMajors()),
                new ArrayList<>(faculty.getMinors())
        );
    }

    public static RequestData getRequestData(Request request) {
        return new RequestData(
                request.getId(),
                request.getType(),
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getResponse(),
                request.getAssigner().getFullName(),
                request.getAssigner().getId(),
                new ArrayList<>(request.getAssignees().stream().map(User::getId).collect(Collectors.toList()))
        );
    }

    public static CaptchaData getCaptchaData(Captcha captcha) {
        return new CaptchaData(
                captcha.getImageText(),
                RawDataHandler.getAttachmentData(captcha.getAttachment())
        );
    }

    public static AttachmentData getAttachmentData(Attachment attachment) {
        if (attachment == null)
            return null;

        return new AttachmentData(
                attachment.getId(),
                FileHandler.readFile(Config.BASE_FILES_PATH + attachment.getFileName()),
                attachment.getFileName()
        );
    }

    public static ScoreData getScoreData(Score score) {
        return new ScoreData(
                score.getId(),
                score.getCourse().getName(),
                score.getCourse().getId(),
                score.getStudent().getFullName(),
                score.getStudent().getId(),
                score.getTemporaryScore(),
                score.getFinalScore(),
                score.isFinalized()
        );
    }

    public static CourseScoringData getCourseScoringData(Course course) {
        return new CourseScoringData(
                course.getTotalGradePointAverage(),
                course.getPassedNumber(),
                course.getFailedNumber(),
                course.getTotalGradePointAverageWithoutFails()
        );
    }

    public static MessageData getMessageData(Message message) {
        return new MessageData(
                message.getId(),
                message.getSender().getFullName(),
                message.getSender().getId(),
                message.getText(),
                new ArrayList<>(message.getAttachments().stream().map(Attachment::getId).toList()),
                message.getChatFeed().getId(),
                message.getRegisterTime()
        );
    }

    public static ChatFeedData getChatFeedData(ChatFeed chatFeed) {
        return new ChatFeedData(
                chatFeed.getId(),
                new ArrayList<>(chatFeed.getUsers().stream().map(User::getId).collect(Collectors.toList())),
                new ArrayList<>(chatFeed.getMessages().stream().map(Message::getId).collect(Collectors.toList()))
        );
    }

    public static AssignmentData getAssignmentData(Assignment assignment) {
        UUID attachmentId = null;
        if (assignment.getAttachment() != null)
            attachmentId = assignment.getAttachment().getId();

        return new AssignmentData(
                assignment.getId(),
                assignment.getCourse().getName(),
                assignment.getCourse().getId(),
                assignment.getStartTime(),
                assignment.getFullScoreTime(),
                assignment.getEndTime(),
                assignment.getName(),
                assignment.getDescription(),
                attachmentId,
                assignment.getAnswerType()
        );
    }

    public static AssignmentAnswerData getAssignmentAnswerData(AssignmentAnswer assignmentAnswer) {
        return new AssignmentAnswerData(
                assignmentAnswer.getId(),
                assignmentAnswer.getAssignment().getName(),
                assignmentAnswer.getAssignment().getId(),
                assignmentAnswer.getStudent().getFullName(),
                assignmentAnswer.getStudent().getId(),
                assignmentAnswer.getAttachment().getId(),
                assignmentAnswer.getText(),
                assignmentAnswer.getSentTime(),
                assignmentAnswer.getScore()
        );
    }

    public static EducationalContentData getEducationalContentData(EducationalContent educationalContent) {
        return new EducationalContentData(
                educationalContent.getId(),
                educationalContent.getName(),
                new ArrayList<>(educationalContent.getTexts()),
                new ArrayList<>(educationalContent.getMedias().stream().map(Attachment::getId).collect(Collectors.toList())),
                educationalContent.getCourse().getId()
        );
    }

    public static void createProfessor(ProfessorData professorData) {
        try {
            DataBase.entityManager.getTransaction().begin();

            Professor professor = new Professor(
                    professorData.nationalId,
                    "placeholder",
                    professorData.email,
                    professorData.type
            );

            professor.setFaculty(DataBase.entityManager.find(Faculty.class, professorData.facultyId));
            professor.setFirstName(professorData.firstName);
            professor.setLastName(professorData.lastName);
            professor.setEmail(professorData.email);
            professor.setPhoneNumber(professorData.phoneNumber);
            professor.setImage(createAttachment(professorData.imageData));

            professor.setRoomNumber(professorData.roomNumber);
            professor.setProfessorNumber(professorData.professorNumber);
            professor.setType(professorData.type);
            professor.setRank(professorData.rank);

            DataBase.entityManager.persist(professor);
            DataBase.entityManager.getTransaction().commit();

            DataBase.addNewUserChatFeeds(professor.getId());

            Logger.Info("Created new professor");

        } catch (Exception e) {
            DataBase.entityManager.getTransaction().rollback();
            e.printStackTrace();
            Logger.Error("Failed to create Professor");
            halt(500);
        }
    }

    public static void createStudent(StudentData studentData) {
        try {
            DataBase.entityManager.getTransaction().begin();

            Student student = new Student(
                    studentData.nationalId,
                    "placeholder",
                    studentData.email,
                    studentData.type
            );

            student.setFaculty(DataBase.entityManager.find(Faculty.class, studentData.facultyId));
            student.setFirstName(studentData.firstName);
            student.setLastName(studentData.lastName);
            student.setEmail(studentData.email);
            student.setPhoneNumber(studentData.phoneNumber);
            student.setImage(createAttachment(studentData.imageData));

            student.setSupervisorProfessor(DataBase.entityManager.find(Professor.class, studentData.supervisorProfessorId));
            student.setEducationalStatus(studentData.educationalStatus);
            student.setType(studentData.type);
            student.setStaringYear(studentData.startingYear);
            student.setMajor(studentData.major);
            student.setStudentNumber(studentData.studentNumber);

            DataBase.entityManager.persist(student);
            DataBase.entityManager.getTransaction().commit();

            DataBase.addNewUserChatFeeds(student.getId());

            Logger.Info("Created new student");

        } catch (Exception e) {
            DataBase.entityManager.getTransaction().rollback();
            e.printStackTrace();
            Logger.Error("Failed to create student");
            halt(500);
        }
    }

    public static void createCourse(CourseData courseData) {
        try {
            DataBase.entityManager.getTransaction().begin();

            Course course = new Course(
                    courseData.name,
                    DataBase.entityManager.find(Faculty.class, courseData.facultyId),
                    DataBase.entityManager.find(Professor.class, courseData.professorId),
                    courseData.size,
                    courseData.level
            );
            course.setTime(courseData.time);
            course.setWeekDays(courseData.weekDays);

            DataBase.entityManager.persist(course);
            DataBase.entityManager.getTransaction().commit();

            Logger.Info("Created new course");
        } catch (Exception e) {
            DataBase.entityManager.getTransaction().rollback();
            e.printStackTrace();
            Logger.Error("Failed to create course");
            halt(500);
        }
    }

    public static void createMessage(MessageData messageData) {
        try {
            DataBase.entityManager.getTransaction().begin();

            var chatFeed = DataBase.entityManager.find(ChatFeed.class, messageData.chatFeedId);

            Message message = new Message(
                    DataBase.entityManager.find(User.class, messageData.senderId),
                    messageData.text,
                    messageData.attachmentsId.stream().map(
                            attachmentId -> DataBase.entityManager.find(Attachment.class, attachmentId)
                    ).collect(Collectors.toList()),
                    chatFeed
            );

            chatFeed.getMessages().add(message);

            DataBase.entityManager.persist(message);
            DataBase.entityManager.getTransaction().commit();

            Logger.Info("created new message");
        } catch (Exception e) {
            DataBase.entityManager.getTransaction().rollback();
            e.printStackTrace();
            Logger.Error("Failed to create new message");
            halt(500);
        }
    }

    public static Attachment createAttachment(AttachmentData attachmentData) {
        try {
            DataBase.entityManager.getTransaction().begin();

            var fileExtension = attachmentData.fileName.split("[.]")[attachmentData.fileName.split("[.]").length - 1];

            Attachment attachment = new Attachment();
            DataBase.entityManager.persist(attachment);
            attachment.setFileName(attachment.getId().toString() + "." + fileExtension);
            DataBase.entityManager.getTransaction().commit();

            FileHandler.writeFile(attachmentData.data, Config.BASE_FILES_PATH + attachment.getId() + "." + fileExtension);

            Logger.Info("created new attachment");

            return attachment;
        } catch (Exception e) {
            DataBase.entityManager.getTransaction().rollback();
            e.printStackTrace();
            Logger.Error("Failed to create new attachment");
            halt(500);
            return null;
        }
    }

    public static void createAssignment(AssignmentData assignmentData) {
        try {
            DataBase.entityManager.getTransaction().begin();

            Assignment assignment = new Assignment(
                    DataBase.entityManager.find(Course.class, assignmentData.courseId),
                    assignmentData.startTime,
                    assignmentData.fullScoreTime,
                    assignmentData.endTime,
                    assignmentData.name,
                    assignmentData.description,
                    DataBase.entityManager.find(Attachment.class, assignmentData.attachmentId),
                    assignmentData.answerType
            );

            DataBase.entityManager.persist(assignment);
            DataBase.entityManager.getTransaction().commit();

            Logger.Info("created new assignment");
        } catch (Exception e) {
            DataBase.entityManager.getTransaction().rollback();
            e.printStackTrace();
            Logger.Error("Failed to create new assignment");
            halt(500);
        }
    }

    public static void createAssignmentAnswer(AssignmentAnswerData assignmentAnswerData) {
        var assignment = DataBase.entityManager.find(Assignment.class, assignmentAnswerData.assignmentId);

        if (assignment.getAnswerType() == AssignmentAcceptableTypes.text && assignmentAnswerData.text != null)
            halt(400);
        if (assignment.getAnswerType() == AssignmentAcceptableTypes.file && assignmentAnswerData.attachmentId != null)
            halt(400);

        try {
            DataBase.entityManager.getTransaction().begin();

            AssignmentAnswer assignmentAnswer = new AssignmentAnswer(
                    assignment,
                    DataBase.entityManager.find(Student.class, assignmentAnswerData.studentId),
                    DataBase.entityManager.find(Attachment.class, assignmentAnswerData.attachmentId),
                    assignmentAnswerData.text
            );

            DataBase.entityManager.persist(assignmentAnswer);
            DataBase.entityManager.getTransaction().commit();

            Logger.Info("created new assignment answer");
        } catch (Exception e) {
            DataBase.entityManager.getTransaction().rollback();
            e.printStackTrace();
            Logger.Error("Failed to create new assignment answer");
            halt(500);
        }
    }

    public static void createEducationalContent(EducationalContentData educationalContentData) {
        try {
            DataBase.entityManager.getTransaction().begin();

            EducationalContent educationalContent = new EducationalContent(
                    educationalContentData.name,
                    educationalContentData.texts,
                    new ArrayList<>(educationalContentData.attachmentsId.stream().map(
                            attachmentId -> DataBase.entityManager.find(Attachment.class, attachmentId)).collect(Collectors.toList())
                    ),
                    DataBase.entityManager.find(Course.class, educationalContentData.courseId)
            );

            DataBase.entityManager.persist(educationalContent);
            DataBase.entityManager.getTransaction().commit();

            Logger.Info("created new educationalContent");
        } catch (Exception e) {
            DataBase.entityManager.getTransaction().rollback();
            e.printStackTrace();
            Logger.Error("Failed to create new educationalContent");
            halt(500);
        }
    }

    public static void updateFaculty(FacultyData facultyData) {
        try {
            DataBase.entityManager.getTransaction().begin();

            var faculty = DataBase.entityManager.find(Faculty.class, facultyData.id);

            if (facultyData.deputyEducationProfessorId == null) {
                faculty.setDeputyEducationProfessor(null);
            } else {
                faculty.setDeputyEducationProfessor(
                        DataBase.entityManager.find(Professor.class, facultyData.deputyEducationProfessorId)
                );
            }

            if (facultyData.facultyHeadProfessorId != null) {
                faculty.setFacultyHeadProfessor(
                        DataBase.entityManager.find(Professor.class, facultyData.facultyHeadProfessorId)
                );
            }

            DataBase.entityManager.getTransaction().commit();

            Logger.Info("created new attachment");

        } catch (Exception e) {
            DataBase.entityManager.getTransaction().rollback();
            e.printStackTrace();
            Logger.Error("Failed to create new attachment");
            halt(500);
        }
    }

    public static void updateScore(ScoreData scoreData) {
        try {
            DataBase.entityManager.getTransaction().begin();

            var score = DataBase.entityManager.find(Score.class, scoreData.id);
            if (scoreData.temporaryScore != null)
                score.setTemporaryScore(scoreData.temporaryScore);

            DataBase.entityManager.getTransaction().commit();

            Logger.Info("created new attachment");

        } catch (Exception e) {
            DataBase.entityManager.getTransaction().rollback();
            e.printStackTrace();
            Logger.Error("Failed to create new attachment");
            halt(500);
        }
    }

    public static void updateUser(UserData userData) {
        try {
            DataBase.entityManager.getTransaction().begin();

            var user = DataBase.entityManager.find(User.class, userData.id);

            if (userData.email != null && !userData.email.equals(user.getEmail()))
                user.setEmail(userData.email);

            if (userData.phoneNumber != null && !userData.phoneNumber.equals(user.getPhoneNumber()))
                user.setEmail(userData.phoneNumber);

            if (userData.imageData != null
                    && userData.imageData.id != null
                    && !userData.imageData.id.equals(user.getImage().getId()))
                user.setImage(DataBase.entityManager.find(Attachment.class, userData.imageData.id));

            DataBase.entityManager.getTransaction().commit();

            Logger.Info("Updated user");

        } catch (Exception e) {
            DataBase.entityManager.getTransaction().rollback();
            e.printStackTrace();
            Logger.Error("Failed to update user");
            halt(500);
        }
    }

    public static void updateProfessor(ProfessorData professorData) {
        try {
            DataBase.entityManager.getTransaction().begin();

            var professor = DataBase.entityManager.find(Professor.class, professorData.id);

            if (professorData.firstName != null && !professorData.firstName.equals(professor.getFirstName()))
                professor.setFirstName(professorData.firstName);

            if (professorData.lastName != null && !professorData.lastName.equals(professor.getLastName()))
                professor.setLastName(professorData.lastName);

            if (professorData.facultyName != null && !professorData.facultyName.equals(professor.getFaculty().getName()))
                professor.setFaculty(DataBase.getFacultyByName(professorData.facultyName));

            if (professorData.email != null && !professorData.email.equals(professor.getEmail()))
                professor.setEmail(professorData.email);

            if (professorData.nationalId != null && !professorData.nationalId.equals(professor.getNationalId()))
                professor.setNationalId(professorData.nationalId);

            if (professorData.phoneNumber != null && !professorData.phoneNumber.equals(professor.getPhoneNumber()))
                professor.setPhoneNumber(professorData.phoneNumber);

            if (professorData.rank != null && !professorData.rank.equals(professor.getRank()))
                professor.setRank(professorData.rank);

            if (professorData.imageData != null
                    && professorData.imageData.id != null
                    && !professorData.imageData.id.equals(professor.getImage().getId()))
                professor.setImage(DataBase.entityManager.find(Attachment.class, professorData.imageData.id));

            if (professorData.roomNumber != null && !professorData.roomNumber.equals(professor.getRoomNumber()))
                professor.setRoomNumber(professorData.roomNumber);

            DataBase.entityManager.getTransaction().commit();

            Logger.Info("Updated course");

        } catch (Exception e) {
            DataBase.entityManager.getTransaction().rollback();
            e.printStackTrace();
            Logger.Error("Failed to Update course");
            halt(500);
        }
    }

    public static void updateCourse(CourseData courseData) {
        try {
            DataBase.entityManager.getTransaction().begin();

            var course = DataBase.entityManager.find(Course.class, courseData.id);

            if (courseData.temporaryScoresSet != null && courseData.temporaryScoresSet)
                course.setTemporaryScoresSet(true);

            if (courseData.facultyId != null && !courseData.facultyId.equals(course.getFaculty().getId()))
                course.setFaculty(DataBase.entityManager.find(Faculty.class, courseData.facultyId));

            if (courseData.name != null && !courseData.name.equals(course.getName()))
                course.setName(courseData.name);

            if (courseData.professorId != null && !courseData.professorId.equals(course.getProfessor().getId()))
                course.setProfessor(DataBase.entityManager.find(Professor.class, courseData.professorId));

            if (courseData.size != null && !courseData.size.equals(course.getSize()))
                course.setSize(courseData.size);

            if (courseData.level != null && !courseData.level.equals(course.getLevel()))
                course.setLevel(courseData.level);

            if (courseData.level != null && !courseData.level.equals(course.getLevel()))
                course.setLevel(courseData.level);

            if (courseData.weekDays != null && courseData.weekDays.size() != 0 && !courseData.weekDays.equals(course.getWeekDays()))
                course.setWeekDays(courseData.weekDays);

            if (courseData.time != null && !courseData.time.equals(course.getTime()))
                course.setTime(courseData.time);

            DataBase.entityManager.getTransaction().commit();

            Logger.Info("Updated course");

        } catch (Exception e) {
            DataBase.entityManager.getTransaction().rollback();
            e.printStackTrace();
            Logger.Error("Failed to Update course");
            halt(500);
        }
    }

    public static void updatePassword(PasswordChangeData passwordChangeData) {
        try {
            DataBase.entityManager.getTransaction().begin();

            var user = DataBase.entityManager.find(User.class, passwordChangeData.userId);
            user.setPassword(passwordChangeData.hashedPassword);

            DataBase.entityManager.getTransaction().commit();

            Logger.Info("Updated user password");

        } catch (Exception e) {
            DataBase.entityManager.getTransaction().rollback();
            e.printStackTrace();
            Logger.Error("Failed to update user password");
            halt(500);
        }
    }

    public static void updateEducationalContent(EducationalContentData educationalContentData) {
        try {
            DataBase.entityManager.getTransaction().begin();

            var educationalContent = DataBase.entityManager.find(EducationalContent.class, educationalContentData.id);

            educationalContent.setTexts(educationalContentData.texts);
            educationalContent.getMedias().clear();
            educationalContent.setMedias(new ArrayList<>() {{
                addAll(educationalContentData.attachmentsId.stream().map(id -> DataBase.entityManager.find(Attachment.class, id)).toList());
            }});

            DataBase.entityManager.getTransaction().commit();

            Logger.Info("Updated educational content");

        } catch (Exception e) {
            DataBase.entityManager.getTransaction().rollback();
            e.printStackTrace();
            Logger.Error("Failed to update educational content");
            halt(500);
        }
    }
}
