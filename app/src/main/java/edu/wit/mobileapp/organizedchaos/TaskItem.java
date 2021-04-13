package edu.wit.mobileapp.organizedchaos;

public class TaskItem
{
    public int complete;
    public String title;
    public String deadlineDate;
    public String deadlineTime;
    public String category;
    public int category_id;
    public String task_text;
    public int repeatSun, repeatMon, repeatTue, repeatWed, repeatThu, repeatFri, repeatSat;
    public int id;


    public TaskItem(int id, int complete, String title, String deadlineDate, String deadlineTime, int repeatSun, int repeatMon, int repeatTue, int repeatWed, int repeatThu, int repeatFri, int repeatSat, String task_text, String category, int category_id)
    {
        this.id = id;
        this.complete = complete;
        this.title = title;
        this.deadlineDate = deadlineDate;
        this.deadlineTime = deadlineTime;
        this.repeatSun = repeatSun;
        this.repeatMon = repeatMon;
        this.repeatTue = repeatTue;
        this.repeatWed = repeatWed;
        this.repeatThu = repeatThu;
        this.repeatFri = repeatFri;
        this.repeatSat = repeatSat;
        this.task_text = task_text;
        this.category = category;
        this.category_id = category_id;
    }

}
