/*******************************************************************************************************
*   Copyright (c) 2014 CA. All rights reserved.
*
*   This software and all information contained therein is confidential and proprietary and shall
*   not be duplicated, used, disclosed or disseminated in any way except as authorized by the
*   applicable license agreement, without the express written permission of CA. All authorized
*   reproductions must be marked with this language.
*
*   EXCEPT AS SET FORTH IN THE APPLICABLE LICENSE AGREEMENT, TO THE EXTENT PERMITTED BY APPLICABLE
*   LAW, CA PROVIDES THIS SOFTWARE WITHOUT WARRANTY OF ANY KIND, INCLUDING WITHOUT LIMITATION, ANY
*   IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL CA
*   BE LIABLE TO THE END USER OR ANY THIRD PARTY FOR ANY LOSS OR DAMAGE, DIRECT OR INDIRECT, FROM
*   THE USE OF THIS SOFTWARE, INCLUDING WITHOUT LIMITATION, LOST PROFITS, BUSINESS INTERRUPTION,
*   GOODWILL, OR LOST DATA, EVEN IF CA IS EXPRESSLY ADVISED OF SUCH LOSS OR DAMAGE.
*
********************************************************************************************************/

package com.ca.servicenow;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.Map;
import java.util.HashMap;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.IllegalArgumentException;

import com.nolio.platform.shared.api.ActionDescriptor;
import com.nolio.platform.shared.api.ActionResult;
import com.nolio.platform.shared.api.ParameterDescriptor;
import com.nolio.platform.shared.api.Password;
import com.nolio.platform.shared.datamodel.Action;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import com.jayway.jsonpath.JsonPath;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import com.ca.nolio.rdk.dto.Operations;
import com.ca.nolio.rdk.dto.VarType;
import com.ca.nolio.rdk.dto.FilterType;
import com.ca.nolio.rdk.dto.InputParam;
import com.ca.nolio.rdk.dto.OutputParam;
import com.ca.nolio.rdk.dto.Authentication;
import com.ca.nolio.rdk.dto.HttpMethod;
import com.ca.nolio.rdk.dto.QOP;
import com.ca.nolio.rdk.dto.CredentialsInfo;
import com.ca.nolio.rdk.dto.exception.ActionException;
import com.ca.nolio.rdk.model.helper.*;
import com.ca.nolio.rdk.model.RestClientManager;
import com.ca.nolio.rdk.dto.HttpRDKResponse;
import com.ca.nolio.rdk.dto.RDKHeader;

import com.ca.nolio.rdk.template.helper.ErrorHelper;
import com.ca.nolio.rdk.template.helper.ReplacementsHelper;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import javax.xml.xpath.XPathConstants;

import net.minidev.json.*;


/**
 *
 * @author Joe Offenberg
 */
@ActionDescriptor(
        name = "ServiceNow - Attach File",
        description = "Attach file to any ServiceNow Record",
        category="ServiceNow." )
    public class AttachFile extends Action {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(AttachFile.class);
    private static final String SCAPE_CHAR = "\\";
    private static final String CHAR_SET = "UTF-8";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ENCODING = "Accept-Encoding";
    private static String WEBSERVICE_OUTPUT = "rdkWebserviceOutput.txt";
    private List<InputParam> inputParameters = null;    
    private List<OutputParam> outputParameters = null;  


                                
    @ParameterDescriptor(
        name = "ServiceNow URL",
        description = "ServiceNow Instance url",
        out = false,
        in = true,
        nullable = false, // parameter not required
        order = 10,
        defaultValueAsString = ""
        )


            private String servicenowurl1;
        @ParameterDescriptor(
        name = "Username",
        description = "The Username to authenticate",
        out = false,
        in = true,
        nullable = false, // parameter not required
        order = 20,
        defaultValueAsString = ""
        )


            private String username2;
        @ParameterDescriptor(
        name = "Password",
        description = "The Password to use",
        out = false,
        in = true,
        nullable = false, // parameter not required
        order = 30,
        defaultValueAsString = ""
        )


            private Password password3;
        @ParameterDescriptor(
        name = "Local File",
        description = "Local file to upload.  Should be a full path to the file on the local file system.",
        out = false,
        in = true,
        nullable = false, // parameter not required
        order = 40,
        defaultValueAsString = ""
        )


            private String localfile4;
        @ParameterDescriptor(
        name = "File Name",
        description = "The name to give the attachment. This parameter is required to post an attachment.",
        out = false,
        in = true,
        nullable = false, // parameter not required
        order = 50,
        defaultValueAsString = ""
        )


            private String filename5;
        @ParameterDescriptor(
        name = "Table",
        description = "Table which includes the record where you are attaching the file. E.g.  change_request.",
        out = false,
        in = true,
        nullable = false, // parameter not required
        order = 60,
        defaultValueAsString = ""
        )


            private String table6;
        @ParameterDescriptor(
        name = "SYS_ID",
        description = "Record you are attaching the file to.",
        out = false,
        in = true,
        nullable = false, // parameter not required
        order = 70,
        defaultValueAsString = ""
        )


            private String sys_id7;
        @ParameterDescriptor(
        name = "content-type",
        description = "MIME  content type of the file you want to attach. E.g. text/plain",
        out = false,
        in = true,
        nullable = false, // parameter not required
        order = 55,
        defaultValueAsString = ""
        )


            private String contenttype8;
    

    private Authentication authType = Authentication.BASIC;


    @ParameterDescriptor(
            name = "Response Body",
                    description = "This output parameter holds the standard response of the request.",
                out = true, 
            in = false     
        )
        
    private String responsebody1;

    @ParameterDescriptor(
            name = "Response Headers",
                    description = "The Response Headers from the request.",
                out = true, 
            in = false     
        )
        
    private String responseheaders2;

    @ParameterDescriptor(
            name = "Response Code",
                    description = "The Response Code.",
                out = true, 
            in = false     
        )
        
    private Integer responsecode3;


    // Getters and Setters
            public String getservicenowurl1() {
        return servicenowurl1;
    } 
    
    public void setservicenowurl1(String servicenowurl1) {
        this.servicenowurl1 = servicenowurl1;
    }
                public String getusername2() {
        return username2;
    } 
    
    public void setusername2(String username2) {
        this.username2 = username2;
    }
                public Password getpassword3() {
        return password3;
    } 
    
    public void setpassword3(Password password3) {
        this.password3 = password3;
    }
                public String getlocalfile4() {
        return localfile4;
    } 
    
    public void setlocalfile4(String localfile4) {
        this.localfile4 = localfile4;
    }
                public String getfilename5() {
        return filename5;
    } 
    
    public void setfilename5(String filename5) {
        this.filename5 = filename5;
    }
                public String gettable6() {
        return table6;
    } 
    
    public void settable6(String table6) {
        this.table6 = table6;
    }
                public String getsys_id7() {
        return sys_id7;
    } 
    
    public void setsys_id7(String sys_id7) {
        this.sys_id7 = sys_id7;
    }
                public String getcontenttype8() {
        return contenttype8;
    } 
    
    public void setcontenttype8(String contenttype8) {
        this.contenttype8 = contenttype8;
    }
        // Getters and Setters
        public String getresponsebody1() {
        return responsebody1;
    }
    
    public void setresponsebody1(String responsebody1) {
        this.responsebody1 = responsebody1;
    }
            public String getresponseheaders2() {
        return responseheaders2;
    }
    
    public void setresponseheaders2(String responseheaders2) {
        this.responseheaders2 = responseheaders2;
    }
            public Integer getresponsecode3() {
        return responsecode3;
    }
    
    public void setresponsecode3(Integer responsecode3) {
        this.responsecode3 = responsecode3;
    }
    
    @Override
    public ActionResult execute() {
        HttpRDKResponse response = new HttpRDKResponse("NO RESPONSE", null, -1);
        try {
            log.debug("A call was received to execute a Restful Action: " + "AttachFile");
            
            File originalFile = new File(localfile4);
	        String encodedBase64;
	        
	        try {
	            @SuppressWarnings("resource")
	            FileInputStream fileInputStreamReader = new FileInputStream(originalFile);
	           byte[] bytes = new byte[(int)originalFile.length()];
	            fileInputStreamReader.read(bytes);
	            encodedBase64 = new String(bytes, "ISO-8859-1");
	        } catch (FileNotFoundException e) {
	            return new ActionResult(false, "File not found: " + localfile4);
	        } catch (IOException e) {
	            e.printStackTrace();
	            return new ActionResult(false, "IO Exception");
	        }
	        
			String rawRequestValue = encodedBase64 ;
            response = new RestClientManager().restCall(HttpMethod.POST, authType, getInput(),
                "${ServiceNow URL}/api/now/attachment/file?table_name=${Table}&table_sys_id=${SYS_ID}&file_name=${File Name}", 30, "", "",
                rawRequestValue, getRequestParameters(), getHeaders());

            log.debug("REST call response: " + response);

                                                    } catch (Exception e) {
            response = new HttpRDKResponse("There was an error trying to execute a REST Call Action: '"
                + "AttachFile" + "' for Action Pack: "   + "", null, -1);
            log.error(response.getContent(), e);
            return new ActionResult(false, response.getContent() + e.getMessage());
        }

        populateOutputParameters(response);
        return getActionResult();
    }   

    private void populateOutputParameters(HttpRDKResponse output) {
        if (StringUtils.isBlank(output.getContent())) {
            log.debug("Output is empty");
        }
        
                   
                    try {
                Pattern pattern = Pattern.compile("[\\s\\S]*");
                System.out.println("Response Body");
                                    Matcher matcher = pattern.matcher(output.getContent());
                                String stringMatch = null;
                if (matcher.find()) {
                    stringMatch = matcher.group();
                }
        
              responsebody1 = String.valueOf(stringMatch);
            } catch (PatternSyntaxException e) {
                log.error("Caught exception during populating output parameter: " + "Response Body" + ". Bad pattern", e);
            } catch (Exception e) {
                log.error("Caught exception during populating output parameter: " + "Response Body", e);
            }
                               
                    try {
                Pattern pattern = Pattern.compile("[\\s\\S]*");
                System.out.println("Response Headers");
                                    RDKHeader[] headers = output.getHeaders();;
                    JSONArray jsonArray = new JSONArray();
                    for(RDKHeader header : headers){
                        JSONObject obj = new JSONObject();
                        obj.put("value", header.getValue());
                        obj.put("header", header.getHeader());
                        jsonArray.add(obj);
                    }
                    
                    Matcher matcher = pattern.matcher(jsonArray.toJSONString());
                                String stringMatch = null;
                if (matcher.find()) {
                    stringMatch = matcher.group();
                }
        
              responseheaders2 = String.valueOf(stringMatch);
            } catch (PatternSyntaxException e) {
                log.error("Caught exception during populating output parameter: " + "Response Headers" + ". Bad pattern", e);
            } catch (Exception e) {
                log.error("Caught exception during populating output parameter: " + "Response Headers", e);
            }
                               
                    log.debug("Response Code: " + output.getResponseCode());
            responsecode3 = output.getResponseCode();
                }
    
    private ActionResult getActionResult() {
            ActionResult actionResult = null;
        try { 
            actionResult = new ActionResult(true, ReplacementsHelper.replaceOutputParameters( ReplacementsHelper.replaceInputParameters( "Execution succeeded", getInput() ), getOutput() ) );
        } catch (Exception e) {
            log.error("There was an error on the replacement of parameters", e);
        }
        

        return actionResult;
    }

    private void createOutputLocationFile(String outputLocation, String content) throws ActionException {
        File outputDirs = new File(outputLocation);
        if (!outputDirs.exists()) {
            try {
                outputDirs.mkdirs();
            } catch (SecurityException se) {
                log.error("Caught security exception while creating output location: " + outputLocation);
                throw new ActionException("Security exception while creating output location: " + se.getMessage(), se);
            }
        }
        
        String fileName = outputLocation + File.separator + WEBSERVICE_OUTPUT;
        try {
            FileWriter fw = new FileWriter(fileName);
            fw.write(content);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            log.error("Caught IO exception during writing to file");
            throw new ActionException("IO exception during writing to file: " + e.getMessage(), e);
        }
    }   
    
    private List<InputParam> getInput() {
                    inputParameters = new ArrayList<InputParam>();
            InputParam p = null;
                    p = new InputParam();
            p.setName("ServiceNow URL");
                                    p.setVariableName("servicenowurl");
            p.setPrefix("");
            p.setType(VarType.String);
                                                                     p.setValue(servicenowurl1);
                                             
            inputParameters.add(p);     
                    p = new InputParam();
            p.setName("Username");
                                    p.setVariableName("username");
            p.setPrefix("");
            p.setType(VarType.String);
                                                                     p.setValue(username2);
                                             
            inputParameters.add(p);     
                    p = new InputParam();
            p.setName("Password");
                                    p.setVariableName("password");
            p.setPrefix("");
            p.setType(VarType.Password);
                                                                                     if(password3 != null) {
                    p.setValue(password3.toString());
                }
                                             
            inputParameters.add(p);     
                    p = new InputParam();
            p.setName("Local File");
                                    p.setVariableName("localfile");
            p.setPrefix("");
            p.setType(VarType.String);
                                                                     p.setValue(localfile4);
                                             
            inputParameters.add(p);     
                    p = new InputParam();
            p.setName("File Name");
                                    p.setVariableName("filename");
            p.setPrefix("");
            p.setType(VarType.String);
                                                                     p.setValue(filename5);
                                             
            inputParameters.add(p);     
                    p = new InputParam();
            p.setName("Table");
                                    p.setVariableName("table");
            p.setPrefix("");
            p.setType(VarType.String);
                                                                     p.setValue(table6);
                                             
            inputParameters.add(p);     
                    p = new InputParam();
            p.setName("SYS_ID");
                                    p.setVariableName("sys_id");
            p.setPrefix("");
            p.setType(VarType.String);
                                                                     p.setValue(sys_id7);
                                             
            inputParameters.add(p);     
                    p = new InputParam();
            p.setName("content-type");
                                    p.setVariableName("contenttype");
            p.setPrefix("");
            p.setType(VarType.String);
                                                                     p.setValue(contenttype8);
                                             
            inputParameters.add(p);     
                
        
        
        return inputParameters;
    }   

    private List<OutputParam> getOutput() {
            if (outputParameters == null) {
            outputParameters = new ArrayList<OutputParam>();
            OutputParam p = null;
                    p = new OutputParam();
            p.setName("Response Body");
                                    p.setVariableName("responsebody");
            p.setType(VarType.String);
            
                            p.setFilterType(FilterType.REGEX);
                        
                                                                     p.setFilterValue( responsebody1 );
                                     
            outputParameters.add(p);                    
                    p = new OutputParam();
            p.setName("Response Headers");
                                    p.setVariableName("responseheaders");
            p.setType(VarType.String);
            
                            p.setFilterType(FilterType.REGEX);
                        
                                                                     p.setFilterValue( responseheaders2 );
                                     
            outputParameters.add(p);                    
                    p = new OutputParam();
            p.setName("Response Code");
                                    p.setVariableName("responsecode");
            p.setType(VarType.Integer);
            
                            p.setFilterType(FilterType.REGEX);
                        
                                                                                     if(responsecode3 != null) {
                    p.setFilterValue( responsecode3.toString() );
                }
                                     
            outputParameters.add(p);                    
                }
            return outputParameters;
    }

    public HashMap getRequestParameters() {
        HashMap reqParams = null;
        
        return reqParams;
    }

    public HashMap getHeaders() {
        HashMap headers = null;
        
        return headers;
    }
}