package io.pivotal.timetracking.domain;

import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TimeEntry {
   @Id
   @GeneratedValue(
      strategy = GenerationType.AUTO
   )
   private Long timeEntryId;
   @Column(
      nullable = false
   )
   private String feName;
   @Column(
      nullable = false
   )
   private String accountName;
   @Column(
      nullable = false
   )
   private Date date;
   @Column(
      nullable = false
   )
   private Double hours;

   protected TimeEntry() {
   }

   public TimeEntry(String feName, String accountName, Date date, Double hours) {
      this.feName = feName;
      this.accountName = accountName;
      this.date = date;
      this.hours = hours;
   }

   public Long getTimeEntryId() {
      return this.timeEntryId;
   }

   public void setTimeEntryId(Long timeEntryId) {
      this.timeEntryId = timeEntryId;
   }

   public String getFeName() {
      return this.feName;
   }

   public void setFeName(String feName) {
      this.feName = feName;
   }

   public String getAccountName() {
      return this.accountName;
   }

   public void setAccountName(String accountName) {
      this.accountName = accountName;
   }

   public Date getDate() {
      return this.date;
   }

   public void setDate(Date date) {
      this.date = date;
   }

   public Double getHours() {
      return this.hours;
   }

   public void setHours(Double hours) {
      this.hours = hours;
   }

   public String toString() {
      return String.format("TimeEntry = [timeEntryId: %d, feName: '%s', accountName: '%s', date: '%tB %te, %tY', hours: %f]", this.timeEntryId, this.feName, this.accountName, this.date, this.date, this.date, this.hours);
   }
}
