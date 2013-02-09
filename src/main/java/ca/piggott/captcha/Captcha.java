package ca.piggott.captcha;

public class Captcha {

	private final byte[] image;

	private final String term;

	private final String type;

	public Captcha(byte[] image, String term, String type) {
		this.image = image;
		this.term = term;
		this.type = type;
	}
	
	public byte[] getImage() {
		return image;
	}

	public String getTerm() {
		return term;
	}

	public String getType() {
		return type;
	}
}
