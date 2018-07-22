package iwb.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import iwb.adapter.ui.ViewAdapter;
import iwb.adapter.ui.extjs.ExtJs3_3;
import iwb.domain.result.W5QueryResult;
import iwb.engine.VcsEngine;
import iwb.util.GenericUtil;
import iwb.util.HttpUtil;
import iwb.util.UserUtil;

@Controller
@RequestMapping("/app")
public class VcsServlet implements InitializingBean {

	private static Logger logger = Logger.getLogger(VcsServlet.class);
	
	@Autowired
	private	VcsEngine vcsEngine;
	
	private	ViewAdapter	ext3_4;
	private	ViewAdapter	webix3_3;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		ext3_4 = new ExtJs3_3();
	//	webix3_3 = new Webix3_3();
	}

	private ViewAdapter getViewAdapter(Map<String, Object> scd, HttpServletRequest request){
		if(request!=null){
			String renderer = request.getParameter("_renderer");
			if(renderer!=null && renderer.equals("webix3_3"))return webix3_3;
		}
		if(scd!=null){
			String renderer = (String)scd.get("_renderer");
			if(renderer!=null && renderer.equals("webix3_3"))return webix3_3;			
		}
		return ext3_4;
	}
	
	@RequestMapping("/ajaxVCSObjectPull")
	public void hndAjaxVCSObjectPull(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
    	int tableId = GenericUtil.uInt(request, "t");
    	int tablePk = GenericUtil.uInt(request, "k");
		logger.info("hndAjaxVCSObjectPull("+tableId+")"); 
		
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	Map m = vcsEngine.vcsClientObjectPull(scd, tableId, tablePk, GenericUtil.uInt(request, "f")!=0);
    	response.getWriter().write(GenericUtil.fromMapToJsonString2Recursive(m));
		response.getWriter().close();
	}
	@RequestMapping("/ajaxVCSObjectPullAll")
	public void hndAjaxVCSObjectPullAll(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
		logger.info("hndAjaxVCSObjectPullAll()"); 
		
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	Map m = vcsEngine.vcsClientObjectPullAll(scd, GenericUtil.uInt(request, "f"), GenericUtil.uInt(request, "c")!=0);
    	response.getWriter().write(GenericUtil.fromMapToJsonString2Recursive(m));
		response.getWriter().close();
	}
	
	
	@RequestMapping("/ajaxVCSObjectPullMulti")
	public void hndAjaxVCSObjectPullMulti(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxVCSObjectPullMulti(1)"); 
		
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	Map m = vcsEngine.vcsClientObjectPullMulti(scd, request.getParameter("k"), GenericUtil.uInt(request, "f")!=0);
    	response.getWriter().write(GenericUtil.fromMapToJsonString2Recursive(m));
		response.getWriter().close();
	}
	

	@RequestMapping("/serverVCSObjectPullMulti")
	public void hndServerVCSObjectPullMulti(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
	
		
		
		String userName = request.getParameter("u");
		String passWord = request.getParameter("p");
		int customizationId = GenericUtil.uInt(request, "c");
		String projectId =request.getParameter("r");
		
		logger.info("hndServerVCSObjectPullMulti(1)"); 
		
    	Map m = vcsEngine.vcsServerObjectPullMulti(userName, passWord, customizationId, projectId, request.getParameter("k"), false, request.getRemoteAddr());


    	response.getWriter().write(GenericUtil.fromMapToJsonString2Recursive(m));
		response.getWriter().close();	
		
	}
	
	@RequestMapping("/ajaxVCSObjectAction")
	public void hndAjaxVCSObjectAction(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
    	int tableId = GenericUtil.uInt(request, "t");
    	int tablePk = GenericUtil.uInt(request, "k");
    	int action = GenericUtil.uInt(request, "a");
		logger.info("hndAjaxVCSObjectAction("+tableId+")"); 
		
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	int cnt = vcsEngine.vcsClientObjectAction(scd, tableId, tablePk, action);
    	response.getWriter().write("{\"success\":"+(cnt==1)+"}");
		response.getWriter().close();
	}

	@RequestMapping("/ajaxVCSObjectsList")
	public void hndAjaxVCSObjectsList(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
    	int tableId = GenericUtil.uInt(request, "_tid");
		logger.info("hndAjaxVCSObjectsList("+tableId+")"); 
		
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	W5QueryResult qr= vcsEngine.vcsClientObjectsList(scd, tableId, 0, 0);
    	response.getWriter().write(getViewAdapter(scd, request).serializeQueryData(qr).toString());
		response.getWriter().close();
	}
	

	@RequestMapping("/ajaxVCSObjectsAll")
	public void hndAjaxVCSObjectsAll(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxVCSObjectsAll"); 
		
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	W5QueryResult qr= vcsEngine.vcsClientObjectsAll(scd);
    	response.getWriter().write(getViewAdapter(scd, request).serializeQueryData(qr).toString());
		response.getWriter().close();
	}
	
	@RequestMapping("/ajaxVCSXRay")
	public void hndAjaxVCSXRay(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxVCSXRay"); 
		
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	W5QueryResult qr= vcsEngine.vcsClientXRay(scd);
    	response.getWriter().write(getViewAdapter(scd, request).serializeQueryData(qr).toString());
		response.getWriter().close();
	}
	
	@RequestMapping("/ajaxVCSFix")
	public void hndAjaxVCSFix(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
		logger.info("hndAjaxVCSFix"); 
    	int tableId = GenericUtil.uInt(request, "t");
    	int action = GenericUtil.uInt(request, "a");
		
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	boolean b = vcsEngine.vcsFix(scd, tableId, action);
    	response.getWriter().write("{\"success\":"+b+"}");
		response.getWriter().close();
	}

	
	@RequestMapping("/ajaxVCSMove2AnotherProject")
	public void hndAjaxVCSSMove2AnotherProject(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
		logger.info("hndAjaxVCSSMove2AnotherProject"); 
    	int tableId = GenericUtil.uInt(request, "t");
    	int tablePk = GenericUtil.uInt(request, "k");
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	boolean b = vcsEngine.vcsClientMove2AnotherProject(scd, request.getParameter("np"), tableId, tablePk);
    	response.getWriter().write("{\"success\":"+b+"}");
		response.getWriter().close();
	}


	@RequestMapping("/ajaxVCSObjectsAllTree")
	public void hndAjaxVCSObjectsAllTree(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxVCSObjectsAll"); 
		
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
		if(!GenericUtil.isEmpty(request.getParameter("anode"))){
	    	response.getWriter().write("{\"success\":false}");
		} else {
	    	W5QueryResult qr= vcsEngine.vcsClientObjectsAllTree(scd, request.getParameter("_schema"), GenericUtil.uInt(request,"_u"), request.getParameter("_ds"), request.getParameter("_de"));
	    	response.getWriter().write(getViewAdapter(scd, request).serializeQueryData(qr).toString());
		}
		response.getWriter().close();
	}
	
	

	@RequestMapping("/ajaxVCSObjectPush")
	public void hndAjaxVCSObjectPush(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
    	int tableId = GenericUtil.uInt(request, "t");
    	int tablePk = GenericUtil.uInt(request, "k");
		logger.info("ajaxVCSObjectPush("+tableId+")"); 
		
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	int commitId = vcsEngine.vcsClientObjectPush(scd, tableId, tablePk, GenericUtil.uInt(request, "f")!=0, false);
    	response.getWriter().write("{\"success\":true, \"commit_id\":"+commitId+"}");
		response.getWriter().close();
	}

	@RequestMapping("/ajaxVCSExportProject")
	public void hndAjaxVCSExportProject(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
		int startCommitId = GenericUtil.uInt(request, "s");
		if(startCommitId<10)startCommitId=10;
		logger.info("hndAjaxVCSExportProject("+startCommitId+")"); 
		
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	boolean b = vcsEngine.vcsClientExportProject(scd, startCommitId);
    	response.getWriter().write("{\"success\":"+b+"}");
		response.getWriter().close();
	}
	
	@RequestMapping("/ajaxVCSObjectPushMulti")
	public void hndAjaxVCSObjectPushMulti(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
    	int tableId = GenericUtil.uInt(request, "t");
    	String tablePks = request.getParameter("k");
		logger.info("ajaxVCSObjectPushMulti("+tableId+")"); 
		
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	int commitId = vcsEngine.vcsClientObjectPushMulti(scd, tableId, tablePks, GenericUtil.uInt(request, "f")!=0, false, true);
    	response.getWriter().write("{\"success\":true, \"commit_id\":"+commitId+"}");
		response.getWriter().close();
	}
	
	@RequestMapping("/ajaxVCSObjectPushAll")
	public void hndAjaxVCSObjectPushAll(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
    	String tableKeys = request.getParameter("k");
		logger.info("hndAjaxVCSObjectPushAll("+tableKeys+")"); 
		
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	int commitId = vcsEngine.vcsClientObjectPushAll(scd,  tableKeys, GenericUtil.uInt(request, "f")!=0, false, true);
    	response.getWriter().write("{\"success\":true, \"commit_id\":"+commitId+"}");
		response.getWriter().close();
	}
	
	@RequestMapping("/serverVCSObjectPull")
	public void hndServerVCSObjectPull(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		String userName = request.getParameter("u");
		String passWord = request.getParameter("p");
		int tableId = GenericUtil.uInt(request, "t");
		int tablePk = GenericUtil.uInt(request, "k");
		int customizationId = GenericUtil.uInt(request, "c");
		int vcsCommitId = GenericUtil.uInt(request, "o");
		String projectId = request.getParameter("r");
		logger.info("hndServerVCSObjectPull("+projectId+")"); 
		
    	Map m = vcsEngine.vcsServerObjectPull(userName, passWord, customizationId, projectId, tableId, tablePk, vcsCommitId);
    	m.put("success", true);
    	response.getWriter().write(GenericUtil.fromMapToJsonString2Recursive(m));
		response.getWriter().close();
	}
	

	@RequestMapping("/serverVCSObjectsDetail")
	public void hndServerVCSObjectsDetail(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
		logger.info("hndServerVCSObjectsDetail"); 
		JSONObject jo = HttpUtil.getJson(request);
		  
		String userName = jo.getString("u")
			   , passWord = jo.getString("p")
			   , projectId = jo.getString("r");
		int customizationId = jo.getInt("c");
		JSONArray objects = jo.getJSONArray("objects");
		
		
    	Map m = vcsEngine.vcsServerObjectsDetail(userName, passWord, customizationId, projectId,  objects);

    	m.put("success", true);
    	response.getWriter().write(GenericUtil.fromMapToJsonString2Recursive(m));
		response.getWriter().close();
	}
	
	
	@RequestMapping("/serverVCSObjectPush")
	public void hndServerVCSObjectPush(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
		
		JSONObject jo = HttpUtil.getJson(request);
		  
		String userName = jo.getString("u")
			   , passWord = jo.getString("p")
			   , projectId = jo.getString("r");
		int tableId =jo.getInt("t")
			, tablePk = jo.getInt("k")
			, action = jo.getInt("a")
			, customizationId = jo.getInt("c");
		
		String comment = GenericUtil.uStr(jo, "comment");
		
		int vcsCommitId = jo.getInt("o");
		boolean force = GenericUtil.uInt(jo, "f")!=0;
		JSONObject object = action!=3 ? jo.getJSONObject("object") : null;
		
		logger.info("hndServerVCSObjectPush("+object.toString()+")"); 
		
    	int commitId = vcsEngine.vcsServerObjectPush(userName, passWord, customizationId, projectId, tableId, tablePk, vcsCommitId, action, force, object, comment);
    	response.getWriter().write("{\"success\":true, \"commit_id\":"+commitId+"}");
		response.getWriter().close();
	}

	@RequestMapping("/serverVCSTenantCheck")
	public void hndServerTenantCheck(HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
		JSONObject json = HttpUtil.getJson(request);
		String email = json.getString("email");
		int socialCon = json.getInt("socialCon");
		String socialNet = json.getString("socialNet");
		String nickname = json.getString("nickname");
		
		Map map = vcsEngine.vcsServerTenantCheck(socialCon, email,nickname,socialNet);
		
		map.put("success", true);
    	response.getWriter().write(GenericUtil.fromMapToJsonString2Recursive(map));
		response.getWriter().close();
	}
	
	@RequestMapping("/serverVCSObjectsList")
	public void hndServerVCSObjectsList(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		String userName = request.getParameter("u");
		String passWord = request.getParameter("p");
		int tableId = GenericUtil.uInt(request, "t");
		int customizationId = GenericUtil.uInt(request, "c");
		int vcsCommitId = GenericUtil.uInt(request, "o");
		String projectId = request.getParameter("r");
		logger.info("hndServerVCSObjectsList("+projectId+")"); 
		
    	Map m = vcsEngine.vcsServerObjectsList(userName, passWord, customizationId, projectId, tableId);
    	m.put("success", true);
    	response.getWriter().write(GenericUtil.fromMapToJsonString2Recursive(m));
		response.getWriter().close();
	}

	
	@RequestMapping("/serverVCSObjectPushMulti")
	public void hndServerVCSObjectPushMulti(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
		JSONObject jo = HttpUtil.getJson(request);
		  
		String userName = jo.getString("u")
			   , passWord = jo.getString("p")
			   , projectId = jo.getString("r");
		int tableId =jo.getInt("t")
			, customizationId = jo.getInt("c");
		String comment = GenericUtil.uStr(jo, "comment");
		
		boolean force = GenericUtil.uInt(jo, "f")!=0;
		JSONArray objects = jo.getJSONArray("objects");
		
		logger.info("hndServerVCSObjectPushMulti("+projectId+")"); 
		
    	int commitId = vcsEngine.vcsServerObjectPushMulti(userName, passWord, customizationId, projectId, tableId, force, objects, comment);

    	response.getWriter().write("{\"success\":true, \"commit_id\":"+commitId+"}");
		response.getWriter().close();	
	}
	
	@RequestMapping("/serverVCSObjectPushAll")
	public void hndServerVCSObjectPushAll(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
		JSONObject jo = HttpUtil.getJson(request);
		  
		String userName = jo.getString("u")
			   , passWord = jo.getString("p")
			   , projectId = jo.getString("r");
		int customizationId = jo.getInt("c");

		boolean force = GenericUtil.uInt(jo, "f")!=0;
		
		String comment = GenericUtil.uStr(jo, "comment");
		
		JSONArray objects = jo.getJSONArray("objects");
		
		logger.info("hndServerVCSObjectPushAll("+projectId+")"); 
		
    	int commitId = vcsEngine.vcsServerObjectPushAll(userName, passWord, customizationId, projectId, force, objects, comment);


    	response.getWriter().write("{\"success\":true, \"commit_id\":"+commitId+"}");
		response.getWriter().close();	
	}
	
	@RequestMapping("/serverVCSObjectsAll")
	public void hndServerVCSObjectsAll(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
		String userName = request.getParameter("u");
		String passWord = request.getParameter("p");
		int customizationId = GenericUtil.uInt(request, "c");
		String projectId = request.getParameter("r");
		String schema = request.getParameter("s");

		logger.info("hndServerVCSObjectsAll("+projectId+")"); 
		
    	Map m = vcsEngine.vcsServerObjectsAll(userName, passWord, customizationId, projectId, schema);
    	m.put("success", true);
    	response.getWriter().write(GenericUtil.fromMapToJsonString2Recursive(m));
		response.getWriter().close();
	}
	
	@RequestMapping("/ajaxVcsClientDBObjectList")
	public void hndAjaxVcsClientDBObjectList(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxVcsClientDBObjectList"); 
		
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	W5QueryResult qr= vcsEngine.vcsClientDBObjectList(scd);
    	response.getWriter().write(getViewAdapter(scd, request).serializeQueryData(qr).toString());
		response.getWriter().close();
	}
	
	@RequestMapping("/serverDBObjectAll")
	public void hndServerVCSDBObjectAll(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		String userName = request.getParameter("u");
		String passWord = request.getParameter("p");
		int customizationId = GenericUtil.uInt(request, "c");
		String projectId = request.getParameter("r");
		logger.info("hndServerVCSDBObjectAll("+projectId+")"); 
		
    	Map m = vcsEngine.serverDBObjectAll(userName, passWord, customizationId, projectId);
    	m.put("success", true);
    	response.getWriter().write(GenericUtil.fromMapToJsonString2Recursive(m));
		response.getWriter().close();
	}
	
	
	@RequestMapping("/serverSQLCommit")
	public void hndServerSQLCommit(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		String userName = request.getParameter("u");
		String passWord = request.getParameter("p");
		int customizationId = GenericUtil.uInt(request, "c");
		String projectId = request.getParameter("r");
		logger.info("hndServerSQLCommit("+projectId+")"); 
		
    	Map m = vcsEngine.vcsServerSQLCommit(userName, passWord, customizationId, projectId, request.getParameter("s"),request.getParameter("comment"));
    	m.put("success", true);
    	response.getWriter().write(GenericUtil.fromMapToJsonString2Recursive(m));
		response.getWriter().close();
	}
	
	@RequestMapping("/ajaxVCSClientSQLCommitsFetchAndRun")
	public void hndAjaxVVCSClientSQLCommitsFetchAndRun(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    
		logger.info("hndAjaxVVCSClientSQLCommitsFetchAndRun"); 
		
    	int cnt = vcsEngine.vcsClientSqlCommitsFetchAndRun(scd);

    	response.getWriter().write("{\"success\":true, \"cnt\":"+cnt+"}");
		response.getWriter().close();	
	}
	
	
	
	@RequestMapping("/ajaxVCSClientSQLCommitList")
	public void hndAjaxVVCSClientSQLCommitList(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    
		logger.info("hndAjaxVVCSClientSQLCommitList"); 
		
    	String r = vcsEngine.vcsClientSqlCommitList(scd);

    	response.getWriter().write(r);
		response.getWriter().close();	
	}
	
	@RequestMapping("/serverVCSQueryResult")
	public void hndServerVCSQueryResult(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		String userName = request.getParameter("u");
		String passWord = request.getParameter("p");
		int customizationId = GenericUtil.uInt(request, "c");
		String projectId = request.getParameter("r");
		int queryId = GenericUtil.uInt(request, "q");
		
		logger.info("hndServerVCSObjectPushAll("+projectId+")"); 
		
    	W5QueryResult qr = vcsEngine.vcsServerQueryResult(userName, passWord, customizationId, projectId, queryId, GenericUtil.getParameterMap(request));


    	response.getWriter().write(ext3_4.serializeQueryData(qr).toString());
		response.getWriter().close();	
	}
	
	
	@RequestMapping("/ajaxVCSClientPushSql")
	public void hndAjaxVCSClientPushSql(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    
		logger.info("hndAjaxVCSClientPushSql"); 
		
    	int cnt = vcsEngine.vcsClientPushSql(scd, GenericUtil.uInt(request, "commit_id"));

    	response.getWriter().write("{\"success\":true, \"cnt\":"+cnt+"}");
		response.getWriter().close();	
	}
	@RequestMapping("/serverVCSAddSql")
	public void hndServerVCSAddSql(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
		JSONObject jo = HttpUtil.getJson(request);

		String userName = jo.getString("u")
				   , passWord = jo.getString("p")
				   , projectId = jo.getString("r");
			int customizationId = jo.getInt("c");
			
		String sql = jo.getString("s");
		String comment = GenericUtil.uStr(jo, "comment");
		
		logger.info("hndServerVCSAddSql("+projectId+")"); 
		
    	int cnt = vcsEngine.vcsServerAddSQL(userName, passWord, customizationId, projectId, sql, comment);


    	response.getWriter().write("{\"success\":true, \"cnt\":"+cnt+"}");
		response.getWriter().close();	
	}
	
	@RequestMapping("/serverVCSExportProject")
	public void hndServerVCSExportProject(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
		
		JSONObject jo = HttpUtil.getJson(request);

		String userName = jo.getString("u")
				   , passWord = jo.getString("p");
			int customizationId = jo.getInt("c");
			

		logger.info("hndServerVCSExportProject"); 
		
    	boolean b = vcsEngine.vcsServerExportProject(userName, passWord, customizationId, jo.getJSONObject("object"));
    	response.getWriter().write("{\"success\":"+b+"}");
		response.getWriter().close();
	}


	@RequestMapping("/ajaxVCSTableConflicts")
	public void hndAjaxVCSTableConflicts(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
    	String tableName =request.getParameter("t");
		logger.info("hndAjaxVCSTableConflicts("+tableName+")"); 
		
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	W5QueryResult qr= vcsEngine.vcsClientTableConflicts(scd, tableName);
    	response.getWriter().write(getViewAdapter(scd, request).serializeQueryData(qr).toString());
		response.getWriter().close();
	}
	
	@RequestMapping("/ajaxVCSDBFuncDetail")
	public void hndAjaxVCSDBFuncDetail(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
    	String dbFuncName =request.getParameter("f");
		logger.info("hndAjaxVCSDBFuncDetail("+dbFuncName+")"); 
		
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	Map m= vcsEngine.vcsClientDBFuncDetail(scd, dbFuncName);
    	m.put("success", true);
    	response.getWriter().write(GenericUtil.fromMapToJsonString2Recursive(m));
		response.getWriter().close();
	}

	@RequestMapping("/ajaxVCSClientCleanVCSObjects")
	public void hndAjaxVCSClientCleanVCSObjects(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
    	int tableId = GenericUtil.uInt(request, "t");
		logger.info("hndAjaxVCSClientCleanVCSObjects("+tableId+")"); 
		
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	int b = vcsEngine.vcsClientCleanVCSObjects(scd, tableId);
    	response.getWriter().write("{\"success\":true, \"cnt\":"+b+"}");
		response.getWriter().close();
	}
	
	@RequestMapping("/ajaxVCSClientSynchLocaleMsg")
	public void hndAjaxVCSClientSynchLocaleMsg(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
    	int tableId = GenericUtil.uInt(request, "t");
		logger.info("hndAjaxVCSClientCleanVCSObjects("+tableId+")"); 
		
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	Map m = vcsEngine.vcsClientLocaleMsgSynch(scd);
    	response.getWriter().write(GenericUtil.fromMapToJsonString2Recursive(m));
		response.getWriter().close();
	}
	
	@RequestMapping("/serverVCSLocaleMsgPushAll")
	public void hndServerVCSLocaleMsgPushAll(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException, JSONException {
		JSONObject jo = HttpUtil.getJson(request);
		  
		String userName = jo.getString("u")
			   , passWord = jo.getString("p")
			   , projectId = jo.getString("r");
		int customizationId = jo.getInt("c");

		JSONArray objects = jo.getJSONArray("objects");
		
		logger.info("hndServerVCSLocaleMsgPushAll("+projectId+")"); 
		
    	int cnt = vcsEngine.vcsServerLocaleMsgPushAll(userName, passWord, customizationId, projectId, objects);


    	response.getWriter().write("{\"success\":true, \"cnt\":"+cnt+"}");
		response.getWriter().close();	
	}
}
