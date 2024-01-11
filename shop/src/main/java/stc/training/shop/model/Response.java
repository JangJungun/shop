package stc.training.shop.model;

import java.util.List;


public class Response {
    private int created;
    private List<ResponseData> data;
	public int getCreated() {
		return created;
	}
	public void setCreated(int created) {
		this.created = created;
	}
	public List<ResponseData> getData() {
		return data;
	}
	public void setData(List<ResponseData> data) {
		this.data = data;
	}
    
    
}
