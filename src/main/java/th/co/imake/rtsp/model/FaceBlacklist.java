package th.co.imake.rtsp.model;

public class FaceBlacklist {
	private Integer profileId;
	private Integer pictureId;
	private byte[] tempate;
	public Integer getProfileId() {
		return profileId;
	}
	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}
	public Integer getPictureId() {
		return pictureId;
	}
	public void setPictureId(Integer pictureId) {
		this.pictureId = pictureId;
	}
	public byte[] getTempate() {
		return tempate;
	}
	public void setTempate(byte[] tempate) {
		this.tempate = tempate;
	}
	
}
