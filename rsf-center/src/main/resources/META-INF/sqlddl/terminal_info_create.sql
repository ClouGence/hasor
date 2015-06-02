<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">  
<mapper namespace="sp_rsf_class">

	<resultMap id="terminalInfoDOMap" type="net.hasor.rsf.center.domain.entity.TerminalInfoDO">
		<result property="appID"          column="appID" />
		<result property="terminalID"     column="terminalID" />
		<result property="terminalSecret" column="terminalSecret" />
		<result property="remoteIP"       column="remoteIP" />
		<result property="remotePort"     column="remotePort" />
		<result property="remoteUnit"     column="remoteUnit" />
		<result property="remoteVersion"  column="remoteVersion" />
	</resultMap>

	<sql id="terminalInfoDOMap_allColumns">
		*
	</sql>

	<select id="terminalInfoDO_getALL" resultMap="terminalInfoDOMap">
		select
			<include refid="terminalInfoDOMap_allColumns" />
		from bx_info
	</select>

</mapper>