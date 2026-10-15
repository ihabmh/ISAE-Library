package observers;

import models.Media;
import models.Student;
import java.util.List;

public class EmailNotificationObserver implements Observer {
    private List<Student> students;

    public EmailNotificationObserver(List<Student> students) {
        this.students = students;
    }

    @Override
    public void update(Media media) {
        for (Student student : students) {
            if (student.isInterestedIn(media)) {
                sendEmail(student, media);
            }
        }
    }

    private void sendEmail(Student student, Media media) {
        System.out.println("Email sent to " + student.getUsername() + " about new media: " + media.getTitle());
    }
}
