package th.co.imake.rtsp.model;

import java.sql.Timestamp;

public class FaceMatching {
	private String matchingId;
	private Integer pictureId;
	private Timestamp timeMatching ;
	private String timeMatchingStr;
	private String pathSource;
	private String fileSource;
	private String percent;
	private String detail;
	private String title;
	private String firstName;
	private String lastName;
	private String clientName;
	private String pathTarget;
	private String fileTarget;

	public String getMatchingId() {
		return matchingId;
	}
	public void setMatchingId(String matchingId) {
		this.matchingId = matchingId;
	}
	public Integer getPictureId() {
		return pictureId;
	}
	public void setPictureId(Integer pictureId) {
		this.pictureId = pictureId;
	}
	public Timestamp getTimeMatching() {
		return timeMatching;
	}
	public void setTimeMatching(Timestamp timeMatching) {
		this.timeMatching = timeMatching;
	}
	public String getTimeMatchingStr() {
		return timeMatchingStr;
	}
	public void setTimeMatchingStr(String timeMatchingStr) {
		this.timeMatchingStr = timeMatchingStr;
	}
	public String getPathSource() {
		return pathSource;
	}
	public void setPathSource(String pathSource) {
		this.pathSource = pathSource;
	}
	public String getFileSource() {
		return fileSource;
	}
	public void setFileSource(String fileSource) {
		this.fileSource = fileSource;
	}
	public String getPercent() {
		return percent;
	}
	public void setPercent(String percent) {
		this.percent = percent;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPathTarget() {
		return pathTarget;
	}
	public void setPathTarget(String pathTarget) {
		this.pathTarget = pathTarget;
	}
	public String getFileTarget() {
		return fileTarget;
	}
	public void setFileTarget(String fileTarget) {
		this.fileTarget = fileTarget;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String fileTarget) {
		this.clientName = fileTarget;
	}
	
	
}
