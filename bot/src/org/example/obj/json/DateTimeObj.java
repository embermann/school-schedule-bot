package org.example.obj.json;

import java.util.Objects;

public class DateTimeObj {
    private final int hour;
    private final int minute;
    private final int second;
    private final int nano;

    public DateTimeObj(int hour, int minute, int second, int nano) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.nano = nano;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public int getNano() {
        return nano;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateTimeObj that = (DateTimeObj) o;
        return hour == that.hour && minute == that.minute && second == that.second && nano == that.nano;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, minute, second, nano);
    }
}
