package cn.feituo.erp.entity;

/**
 * @author hsy
 *
 */
public class Ordersingoods {
	private Long ouuid;
	private Long uuid;
	private String name;
	private Double inprice;
	private Double outprice;
	private Long ordersnum;
	private Long storedetailnum;
	
	public Long getStoredetailnum() {
		return storedetailnum;
	}

	public void setStoredetailnum(Long storedetailnum) {
		this.storedetailnum = storedetailnum;
	}

	public Long getOuuid() {
		return ouuid;
	}

	public void setOuuid(Long ouuid) {
		this.ouuid = ouuid;
	}

	public Long getUuid() {
		return uuid;
	}

	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getInprice() {
		return inprice;
	}

	public void setInprice(Double inprice) {
		this.inprice = inprice;
	}

	public Double getOutprice() {
		return outprice;
	}

	public void setOutprice(Double outprice) {
		this.outprice = outprice;
	}

	public Long getOrdersnum() {
		return ordersnum;
	}

	public void setOrdersnum(Long ordersnum) {
		this.ordersnum = ordersnum;
	}

	

}
