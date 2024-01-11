package stc.training.shop.model;

import lombok.Data;

@Data
public class ResponseData {
    private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
    
    
}
