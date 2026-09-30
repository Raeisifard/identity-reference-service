package com.isc.identityreference.admin;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix="identity-reference.admin-console")
public class AdminConsoleProperties {
    private boolean enabled=true;
    private String title="Identity Reference Service Admin Console";
    private String path="/admin-console";
    private boolean developmentMode;
    private Authentication authentication=new Authentication();
    private Sections sections=new Sections();
    private HelpCenter helpCenter=new HelpCenter();
    private TestData testData=new TestData();
    public boolean isEnabled(){return enabled;} public void setEnabled(boolean v){enabled=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getPath(){return path;} public void setPath(String v){path=v;}
    public boolean isDevelopmentMode(){return developmentMode;} public void setDevelopmentMode(boolean v){developmentMode=v;}
    public Authentication getAuthentication(){return authentication;} public void setAuthentication(Authentication v){authentication=v;}
    public Sections getSections(){return sections;} public void setSections(Sections v){sections=v;}
    public HelpCenter getHelpCenter(){return helpCenter;} public void setHelpCenter(HelpCenter v){helpCenter=v;}
    public TestData getTestData(){return testData;} public void setTestData(TestData v){testData=v;}
    public static class Authentication{private boolean enabled;private boolean loginPageEnabled=true;private long sessionTimeoutMinutes=30;
        public boolean isEnabled(){return enabled;}public void setEnabled(boolean v){enabled=v;}public boolean isLoginPageEnabled(){return loginPageEnabled;}public void setLoginPageEnabled(boolean v){loginPageEnabled=v;}public long getSessionTimeoutMinutes(){return sessionTimeoutMinutes;}public void setSessionTimeoutMinutes(long v){sessionTimeoutMinutes=v;}}
    public static class Sections{
        private boolean dashboard=true,monitoring=true,providers=true,cache=true,refresh=true,lookup=true,administration=true,biometric=true,testData,apiTesting=true,governance=true,audit=true,security=true,system=true,help=true;
        public boolean isDashboard(){return dashboard;}public void setDashboard(boolean v){dashboard=v;}public boolean isMonitoring(){return monitoring;}public void setMonitoring(boolean v){monitoring=v;}public boolean isProviders(){return providers;}public void setProviders(boolean v){providers=v;}public boolean isCache(){return cache;}public void setCache(boolean v){cache=v;}public boolean isRefresh(){return refresh;}public void setRefresh(boolean v){refresh=v;}public boolean isLookup(){return lookup;}public void setLookup(boolean v){lookup=v;}public boolean isAdministration(){return administration;}public void setAdministration(boolean v){administration=v;}public boolean isBiometric(){return biometric;}public void setBiometric(boolean v){biometric=v;}public boolean isTestData(){return testData;}public void setTestData(boolean v){testData=v;}public boolean isApiTesting(){return apiTesting;}public void setApiTesting(boolean v){apiTesting=v;}public boolean isGovernance(){return governance;}public void setGovernance(boolean v){governance=v;}public boolean isAudit(){return audit;}public void setAudit(boolean v){audit=v;}public boolean isSecurity(){return security;}public void setSecurity(boolean v){security=v;}public boolean isSystem(){return system;}public void setSystem(boolean v){system=v;}public boolean isHelp(){return help;}public void setHelp(boolean v){help=v;}}
    public static class HelpCenter{private boolean enabled=true;public boolean isEnabled(){return enabled;}public void setEnabled(boolean v){enabled=v;}}
    public static class TestData{
        private long maxPhotoBytes=5242880;private int maxPhotoWidth=4096,maxPhotoHeight=4096;private String[] allowedContentTypes={"image/jpeg","image/png"};private boolean allowPurge,biometricEnabled;private String biometricModelId="mock-arcface",biometricModelVersion="dev";private int biometricDimension=512;
        public long getMaxPhotoBytes(){return maxPhotoBytes;}public void setMaxPhotoBytes(long v){maxPhotoBytes=v;}public int getMaxPhotoWidth(){return maxPhotoWidth;}public void setMaxPhotoWidth(int v){maxPhotoWidth=v;}public int getMaxPhotoHeight(){return maxPhotoHeight;}public void setMaxPhotoHeight(int v){maxPhotoHeight=v;}public String[] getAllowedContentTypes(){return allowedContentTypes;}public void setAllowedContentTypes(String[] v){allowedContentTypes=v;}public boolean isAllowPurge(){return allowPurge;}public void setAllowPurge(boolean v){allowPurge=v;}public boolean isBiometricEnabled(){return biometricEnabled;}public void setBiometricEnabled(boolean v){biometricEnabled=v;}public String getBiometricModelId(){return biometricModelId;}public void setBiometricModelId(String v){biometricModelId=v;}public String getBiometricModelVersion(){return biometricModelVersion;}public void setBiometricModelVersion(String v){biometricModelVersion=v;}public int getBiometricDimension(){return biometricDimension;}public void setBiometricDimension(int v){biometricDimension=v;}
    }
}
@Configuration(proxyBeanMethods=false)
@EnableConfigurationProperties(AdminConsoleProperties.class)
class AdminConsoleConfiguration{
    @org.springframework.context.annotation.Bean
    Object validateAdminConsole(AdminConsoleProperties p){
        if(p.isEnabled()&&!p.isDevelopmentMode()&&p.getSections().isTestData())throw new IllegalStateException("Test-data console is DEV/TEST only");
        if(p.isEnabled()&&p.getAuthentication().isEnabled()&&p.getAuthentication().getSessionTimeoutMinutes()<=0)throw new IllegalStateException("Admin console session timeout must be positive");
        if(p.getTestData().getMaxPhotoBytes()<=0||p.getTestData().getMaxPhotoWidth()<=0||p.getTestData().getMaxPhotoHeight()<=0)throw new IllegalStateException("Photo limits must be positive");
        return new Object();
    }
}
@Controller
@ConditionalOnProperty(prefix="identity-reference.admin-console",name="enabled",havingValue="true",matchIfMissing=true)
class AdminConsoleController{@GetMapping({"/admin-console","/admin-console/"})String index(){return "redirect:/admin-console/index.html";}}
@RestController
@RequestMapping("/api/v1/admin/console")
@ConditionalOnProperty(prefix="identity-reference.admin-console",name="enabled",havingValue="true",matchIfMissing=true)
class AdminConsoleRuntimeController{
    private final AdminConsoleProperties p; AdminConsoleRuntimeController(AdminConsoleProperties p){this.p=p;}
    @GetMapping("/config") Map<String,Object> config(){
        var r=new LinkedHashMap<String,Object>();r.put("title",p.getTitle());r.put("developmentMode",p.isDevelopmentMode());r.put("authenticationEnabled",p.getAuthentication().isEnabled());r.put("helpCenterEnabled",p.getHelpCenter().isEnabled());
        var s=p.getSections();var m=new LinkedHashMap<String,Boolean>();m.put("dashboard",s.isDashboard());m.put("monitoring",s.isMonitoring());m.put("providers",s.isProviders());m.put("cache",s.isCache());m.put("refresh",s.isRefresh());m.put("lookup",s.isLookup());m.put("administration",s.isAdministration());m.put("biometric",s.isBiometric());m.put("test-data",s.isTestData());m.put("api-testing",s.isApiTesting());m.put("governance",s.isGovernance());m.put("audit",s.isAudit());m.put("security",s.isSecurity());m.put("system",s.isSystem());m.put("help",s.isHelp());r.put("sections",m);return r;
    }
}
