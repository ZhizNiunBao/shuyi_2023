package cn.bywin.business.bean.view.federal;

import cn.bywin.business.common.base.SidEntityDo;

import lombok.Data;

/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-27
 */
@Data

public class FModelElementVo extends SidEntityDo {

	private String componentId;


	private String ports;


	private String name;


	private String modelId;

	private String config;

	private String x;


	private String y;

	private Integer types;

	private String status;

	private String shape;

	private String icon;


	private String creatorId;


	private String creatorAccount;

	private String creatorName;

	private String componentType;

}
