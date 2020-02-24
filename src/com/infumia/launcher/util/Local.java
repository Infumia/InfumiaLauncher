package com.infumia.launcher.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author Ammar Ahmad
 */
public class Local {

    List versions_json_path_list = new ArrayList(); //gets the path of all json files
    List versions_list = new ArrayList();           //just gets the versions available on the system
    public List version_url_list = new ArrayList();        //gets url of all the libraries
    public List HALF_URL_version_url_list = new ArrayList();// this is the half url. it needs to be fixed first in order to be used

    public List version_path_list = new ArrayList();       //%new added... This is for direct paths
    public List version_name_list = new ArrayList();       //%new added... This is for direct names
    public List version_size_list = new ArrayList();       //%new added... This is for direct names
    public List version_hash_list = new ArrayList();       //%new added... This is for direct names

    List objects_hash = new ArrayList();            //gets objects hash
    List objects_KEY = new ArrayList();             //gets objects keys

    List profiles_lastVersionId = new ArrayList();   //gets profiles lastVersionId
    List profiles_KEY = new ArrayList();             //gets profiles keys

    public List version_url_list_natives = new ArrayList();    //gets url of all the natives
    public List version_path_list_natives = new ArrayList();    //%gets url of all the natives
    List version_name_list_natives = new ArrayList(); //EXP CODE!
    public List version_size_list_natives = new ArrayList(); //EXP CODE!
    public List version_hash_list_natives = new ArrayList(); //EXP CODE!

    public List libraries_path = new ArrayList();          //gets path to all the libraries
    //List natives_path = new ArrayList();            //_NOT NEEDED_ gets path to all the natives

    public List version_manifest_versions_id = new ArrayList();
    List version_manifest_versions_type = new ArrayList();
    public List version_manifest_versions_url = new ArrayList();

    public List getAPIMetaList(String OS) {
        List meta = new ArrayList();
        Utils utils = new Utils();
        try {
            for (String line : Files.readAllLines(Paths.get(utils.getMineCraft_APIMeta(OS)))) {
                for (String part : line.split(":")) {
                    meta.add(part);
                }
            }
        } catch (IOException ex) {
            System.out.println("API Meta does not exist!");
        }
        return meta;
    }

    public void fixLauncherProfiles(String OS) {
        //this is where we will check if file is available or not.
        //if the file is available, we do not need to fix anything...
        //else we need to fix it using the new version.
        Utils utils = new Utils();
        try {

            String content = "{\"profiles\":{\"TagCraftMC\":{\"name\":\"TagCraftMC\"}},\"selectedProfile\":\"TagCraftMC\",\"clientToken\":\"dc291e47-a41f-4bc8-8ec2-563195188db2\",\"authenticationDatabase\":{\"4db2fbf560f355492dea6962e103f1d2\":{\"displayName\":\"TagCraftMC\",\"userProperties\":[{\"name\":\"twitch_access_token\",\"value\":\"e4u4updugw2h7pn7psy3u4u4u7p8raq\"}],\"accessToken\":\"c8c8358cac8a43c896ec85ee3c807c8e\",\"userid\":\"793addf9682c8e04c8cc823db79c8c85\",\"uuid\":\"4db2fbf5-60f3-5549-2dea-6962e103f1d2\",\"username\":\"support@tagcraftmc.com\"}},\"selectedUser\":\"4db2fbf560f355492dea6962e103f1d2\",\"launcherVersion\":{\"name\":\"1.6.61\",\"format\":18}}";

            File file = new File(utils.getMineCraft_Launcher_Profiles_json(OS));

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(content);
                bw.close();
                System.out.println("LauncherProfiles.json Created!");
            } else {
                System.out.println("LauncherProfiles.json not created! File exists!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readJson_profiles_KEY(String path) {
        JSONParser readMCJSONFiles = new JSONParser();
        try {
            Object jsonfile;

            jsonfile = readMCJSONFiles.parse(new FileReader(path));

            JSONObject jsonObject = (JSONObject) jsonfile;
            JSONObject profiles = (JSONObject) jsonObject.get("profiles");

            Set fileCheckObjects = profiles.keySet();
            Iterator a = fileCheckObjects.iterator();
            while (a.hasNext()) {
                String fileName = (String) a.next();
                profiles_KEY.add(fileName);
            }
        } catch (FileNotFoundException exception) {
            System.out.println(exception);
        } catch (IOException | ParseException ex) {
            System.out.println(ex);
        }

    }

    public void readJson_profiles_KEY_lastVersionId(String path) {
        JSONParser readMCJSONFiles = new JSONParser();
        try {
            Object jsonfile;

            jsonfile = readMCJSONFiles.parse(new FileReader(path));

            JSONObject jsonObject = (JSONObject) jsonfile;
            JSONObject profiles = (JSONObject) jsonObject.get("profiles");

            Set fileCheckObjects = profiles.keySet();
            Iterator a = fileCheckObjects.iterator();
            while (a.hasNext()) {
                String fileName = (String) a.next();

                JSONObject fileNameObject = (JSONObject) profiles.get(fileName);
                String lastVersionId = (String) fileNameObject.get("lastVersionId");
                profiles_lastVersionId.add(lastVersionId);
            }
        } catch (FileNotFoundException exception) {
            System.out.println(exception);
        } catch (IOException | ParseException ex) {
            System.out.println(ex);
        }

    }

    /*
    public void writeJson_launcher_profiles_Sync() {
        //step 1 would be to populate the version list.
        //step 2 would be to load json and read profiles{ {val} { lastVersionId } }
    }
     */
    public void writeJson_launcher_profiles(String OS, String profile, String version) {
        try {
            Utils utils = new Utils();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(utils.getMineCraft_Launcher_Profiles_json(OS)));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject profiles = (JSONObject) jsonObject.get("profiles");
            String selectedProfile = (String) jsonObject.get("selectedProfile");
            String clientToken = (String) jsonObject.get("clientToken");
            JSONObject authenticationDatabase = (JSONObject) jsonObject.get("authenticationDatabase");
            String selectedUser = (String) jsonObject.get("selectedUser");
            JSONObject launcherVersion = (JSONObject) jsonObject.get("launcherVersion");

            JSONObject params = new JSONObject();

            params.put("lastVersionId", version);
            params.put("name", profile);
            profiles.put(profile, params);

            JSONObject lp_json = new JSONObject();
            lp_json.put("profiles", profiles);
            lp_json.put("selectedProfile", selectedProfile);
            lp_json.put("clientToken", clientToken);
            lp_json.put("authenticationDatabase", authenticationDatabase);
            lp_json.put("selectedUser", selectedUser);
            lp_json.put("launcherVersion", launcherVersion);
            FileWriter file = new FileWriter(utils.getMineCraft_Launcher_Profiles_json(OS));
            file.write(lp_json.toJSONString());
            file.flush();
            file.close();

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public void readJson_versions_id(String path) {
        JSONParser readMCJSONFiles = new JSONParser();
        try {
            Object object = readMCJSONFiles.parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject) object;
            JSONArray versions = (JSONArray) jsonObject.get("versions");
            Iterator<JSONObject> iterator = versions.iterator();
            while (iterator.hasNext()) {
                JSONObject versions_ = (JSONObject) iterator.next();
                version_manifest_versions_id.add(versions_.get("id"));
                //System.out.println(versions_.get("id"));
            }
        } catch (IOException | ParseException e) {
            //System.out.print(e);
        }
    }

    public void readJson_versions_type(String path) {
        JSONParser readMCJSONFiles = new JSONParser();
        try {
            Object object = readMCJSONFiles.parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject) object;
            JSONArray versions = (JSONArray) jsonObject.get("versions");
            Iterator<JSONObject> iterator = versions.iterator();
            while (iterator.hasNext()) {
                JSONObject versions_ = (JSONObject) iterator.next();
                version_manifest_versions_type.add(versions_.get("type"));
                //System.out.println(versions_.get("id"));
            }
        } catch (IOException | ParseException e) {
            //System.out.print(e);
        }
    }

    public void readJson_versions_url(String path) {
        JSONParser readMCJSONFiles = new JSONParser();
        try {
            Object object = readMCJSONFiles.parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject) object;
            JSONArray versions = (JSONArray) jsonObject.get("versions");
            Iterator<JSONObject> iterator = versions.iterator();
            while (iterator.hasNext()) {
                JSONObject versions_ = (JSONObject) iterator.next();
                version_manifest_versions_url.add(versions_.get("url"));
                //System.out.println(versions_.get("id"));
            }
        } catch (IOException | ParseException e) {
            //System.out.print(e);
        }
    }


    public void readJson_libraries_name(String path, String os) {
        JSONParser readMCJSONFiles = new JSONParser();
        try {
            Object object = readMCJSONFiles.parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject) object;
            JSONArray versions = (JSONArray) jsonObject.get("libraries");
            Iterator<JSONObject> iterator = versions.iterator();
            outer: while (iterator.hasNext()) {
                JSONObject name_ =  iterator.next();
                if (os.toLowerCase().equals("windows")) {
                    if (name_.get("rules") != null) {
                        JSONArray rulesArray = (JSONArray) name_.get("rules");
                        for (Object o : rulesArray) {
                            JSONObject rule = (JSONObject) o;
                            String action = (String) rule.get("action");
                            String osName = "empty";
                            if (rule.get("os") != null) osName = (String) ((JSONObject) rule.get("os")).get("name");
                            if (action.equals("allow")) {
                                if (!osName.equals("empty") && !osName.equals("windows")) {
                                    continue outer;
                                }
                            } else {
                                if (osName.equals("windows")) {
                                    continue outer;
                                }
                            }
                        }
                    }
                }
                if (os.toLowerCase().equals("linux")) {
                    if (name_.get("rules") != null) {
                        JSONArray rulesArray = (JSONArray) name_.get("rules");
                        for (Object o : rulesArray) {
                            JSONObject rule = (JSONObject) o;
                            String action = (String) rule.get("action");
                            String osName = "empty";
                            if (rule.get("os") != null) osName = (String) ((JSONObject) rule.get("os")).get("name");
                            if (action.equals("allow")) {
                                if (!osName.equals("empty") && !osName.equals("linux")) {
                                    continue outer;
                                }
                            } else {
                                if (osName.equals("linux")) {
                                    continue outer;
                                }
                            }
                        }
                    }
                }
                if (os.toLowerCase().equals("mac")) {
                    if (name_.get("rules") != null) {
                        JSONArray rulesArray = (JSONArray) name_.get("rules");
                        for (Object o : rulesArray) {
                            JSONObject rule = (JSONObject) o;
                            String action = (String) rule.get("action");
                            String osName = "empty";
                            if (rule.get("os") != null) osName = (String) ((JSONObject) rule.get("os")).get("name");
                            if (action.equals("allow")) {
                                if (!osName.equals("empty") && !osName.equals("osx")) {
                                    continue outer;
                                }
                            } else {
                                if (osName.equals("osx")) {
                                    continue outer;
                                }
                            }
                        }
                    }
                }
                version_name_list.add(name_.get("name"));
            }
        } catch (IOException | ParseException e) {
            //System.out.print(e);
        }
    }

    public void readJson_libraries_downloads_artifact_size(String path, String os) {

        JSONParser readMCJSONFiles = new JSONParser();
        try {
            Object object = readMCJSONFiles.parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject) object;
            JSONArray msg = (JSONArray) jsonObject.get("libraries");
            Iterator<JSONObject> iterator = msg.iterator();
            outer: while (iterator.hasNext()) {
                JSONObject name_ = (JSONObject) iterator.next();
                if (os.toLowerCase().equals("windows")) {
                    if (name_.get("rules") != null) {
                        JSONArray rulesArray = (JSONArray) name_.get("rules");
                        for (Object o : rulesArray) {
                            JSONObject rule = (JSONObject) o;
                            String action = (String) rule.get("action");
                            String osName = "empty";
                            if (rule.get("os") != null) osName = (String) ((JSONObject) rule.get("os")).get("name");
                            if (action.equals("allow")) {
                                if (!osName.equals("empty") && !osName.equals("windows")) {
                                    continue outer;
                                }
                            } else {
                                if (osName.equals("windows")) {
                                    continue outer;
                                }
                            }
                        }
                    }
                }
                if (os.toLowerCase().equals("linux")) {
                    if (name_.get("rules") != null) {
                        JSONArray rulesArray = (JSONArray) name_.get("rules");
                        for (Object o : rulesArray) {
                            JSONObject rule = (JSONObject) o;
                            String action = (String) rule.get("action");
                            String osName = "empty";
                            if (rule.get("os") != null) osName = (String) ((JSONObject) rule.get("os")).get("name");
                            if (action.equals("allow")) {
                                if (!osName.equals("empty") && !osName.equals("linux")) {
                                    continue outer;
                                }
                            } else {
                                if (osName.equals("linux")) {
                                    continue outer;
                                }
                            }
                        }
                    }
                }
                if (os.toLowerCase().equals("mac")) {
                    if (name_.get("rules") != null) {
                        JSONArray rulesArray = (JSONArray) name_.get("rules");
                        for (Object o : rulesArray) {
                            JSONObject rule = (JSONObject) o;
                            String action = (String) rule.get("action");
                            String osName = "empty";
                            if (rule.get("os") != null) osName = (String) ((JSONObject) rule.get("os")).get("name");
                            if (action.equals("allow")) {
                                if (!osName.equals("empty") && !osName.equals("osx")) {
                                    continue outer;
                                }
                            } else {
                                if (osName.equals("osx")) {
                                    continue outer;
                                }
                            }
                        }
                    }
                }
                JSONObject downloads = (JSONObject) name_.get("downloads");
                if (downloads.get("artifact") != null) {
                    JSONObject artifact = (JSONObject) downloads.get("artifact");
                    if (artifact.get("size") != null) {
                        long size = (long) artifact.get("size");
                        version_size_list.add(size);
                    }
                }
            }
        } catch (IOException | ParseException e) {
            //System.out.print(e);
        }

    }

    public void readJson_libraries_downloads_artifact_hash(String path, String os) {

        JSONParser readMCJSONFiles = new JSONParser();
        try {
            Object object = readMCJSONFiles.parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject) object;
            JSONArray msg = (JSONArray) jsonObject.get("libraries");
            Iterator<JSONObject> iterator = msg.iterator();
            outer: while (iterator.hasNext()) {
                JSONObject name_ = iterator.next();
                if (os.toLowerCase().equals("windows")) {
                    if (name_.get("rules") != null) {
                        JSONArray rulesArray = (JSONArray) name_.get("rules");
                        for (Object o : rulesArray) {
                            JSONObject rule = (JSONObject) o;
                            String action = (String) rule.get("action");
                            String osName = "empty";
                            if (rule.get("os") != null) osName = (String) ((JSONObject) rule.get("os")).get("name");
                            if (action.equals("allow")) {
                                if (!osName.equals("empty") && !osName.equals("windows")) {
                                    continue outer;
                                }
                            } else {
                                if (osName.equals("windows")) {
                                    continue outer;
                                }
                            }
                        }
                    }
                }
                if (os.toLowerCase().equals("linux")) {
                    if (name_.get("rules") != null) {
                        JSONArray rulesArray = (JSONArray) name_.get("rules");
                        for (Object o : rulesArray) {
                            JSONObject rule = (JSONObject) o;
                            String action = (String) rule.get("action");
                            String osName = "empty";
                            if (rule.get("os") != null) osName = (String) ((JSONObject) rule.get("os")).get("name");
                            if (action.equals("allow")) {
                                if (!osName.equals("empty") && !osName.equals("linux")) {
                                    continue outer;
                                }
                            } else {
                                if (osName.equals("linux")) {
                                    continue outer;
                                }
                            }
                        }
                    }
                }
                if (os.toLowerCase().equals("mac")) {
                    if (name_.get("rules") != null) {
                        JSONArray rulesArray = (JSONArray) name_.get("rules");
                        for (Object o : rulesArray) {
                            JSONObject rule = (JSONObject) o;
                            String action = (String) rule.get("action");
                            String osName = "empty";
                            if (rule.get("os") != null) osName = (String) ((JSONObject) rule.get("os")).get("name");
                            if (action.equals("allow")) {
                                if (!osName.equals("empty") && !osName.equals("osx")) {
                                    continue outer;
                                }
                            } else {
                                if (osName.equals("osx")) {
                                    continue outer;
                                }
                            }
                        }
                    }
                }
                JSONObject downloads = (JSONObject) name_.get("downloads");
                if (downloads.get("artifact") != null) {
                    JSONObject artifact = (JSONObject) downloads.get("artifact");
                    if (artifact.get("sha1") != null) {
                        String hash = (String) artifact.get("sha1");
                        version_hash_list.add(hash);
                    }
                }
            }
        } catch (IOException | ParseException e) {
            //System.out.print(e);
        }

    }

    public void readJson_libraries_downloads_artifact_url(String path, String os) {

        JSONParser readMCJSONFiles = new JSONParser();
        try {
            Object object = readMCJSONFiles.parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject) object;
            JSONArray msg = (JSONArray) jsonObject.get("libraries");
            Iterator<JSONObject> iterator = msg.iterator();
            outer: while (iterator.hasNext()) {
                JSONObject name_ = (JSONObject) iterator.next();
                if (os.toLowerCase().equals("windows")) {
                    if (name_.get("rules") != null) {
                        JSONArray rulesArray = (JSONArray) name_.get("rules");
                        for (Object o : rulesArray) {
                            JSONObject rule = (JSONObject) o;
                            String action = (String) rule.get("action");
                            String osName = "empty";
                            if (rule.get("os") != null) osName = (String) ((JSONObject) rule.get("os")).get("name");
                            if (action.equals("allow")) {
                                if (!osName.equals("empty") && !osName.equals("windows")) {
                                    continue outer;
                                }
                            } else {
                                if (osName.equals("windows")) {
                                    continue outer;
                                }
                            }
                        }
                    }
                }
                if (os.toLowerCase().equals("linux")) {
                    if (name_.get("rules") != null) {
                        JSONArray rulesArray = (JSONArray) name_.get("rules");
                        for (Object o : rulesArray) {
                            JSONObject rule = (JSONObject) o;
                            String action = (String) rule.get("action");
                            String osName = "empty";
                            if (rule.get("os") != null) osName = (String) ((JSONObject) rule.get("os")).get("name");
                            if (action.equals("allow")) {
                                if (!osName.equals("empty") && !osName.equals("linux")) {
                                    continue outer;
                                }
                            } else {
                                if (osName.equals("linux")) {
                                    continue outer;
                                }
                            }
                        }
                    }
                }
                if (os.toLowerCase().equals("mac")) {
                    if (name_.get("rules") != null) {
                        JSONArray rulesArray = (JSONArray) name_.get("rules");
                        for (Object o : rulesArray) {
                            JSONObject rule = (JSONObject) o;
                            String action = (String) rule.get("action");
                            String osName = "empty";
                            if (rule.get("os") != null) osName = (String) ((JSONObject) rule.get("os")).get("name");
                            if (action.equals("allow")) {
                                if (!osName.equals("empty") && !osName.equals("osx")) {
                                    continue outer;
                                }
                            } else {
                                if (osName.equals("osx")) {
                                    continue outer;
                                }
                            }
                        }
                    }
                }
                JSONObject downloads = (JSONObject) name_.get("downloads");
                if (downloads.get("artifact") != null) {
                    JSONObject artifact = (JSONObject) downloads.get("artifact");
                    if (artifact.get("url") != null) {
                        String url = (String) artifact.get("url");
                        version_url_list.add(url);
                    }
                }
            }
        } catch (IOException | ParseException e) {
            //System.out.print(e);
        }

    }

    public void readJson_libraries_downloads_artifact_path(String path, String os) {

        JSONParser readMCJSONFiles = new JSONParser();
        try {
            Object object = readMCJSONFiles.parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject) object;
            JSONArray msg = (JSONArray) jsonObject.get("libraries");
            Iterator<JSONObject> iterator = msg.iterator();
            outer: while (iterator.hasNext()) {
                JSONObject name_ = iterator.next();
                if (os.toLowerCase().equals("windows")) {
                    if (name_.get("rules") != null) {
                        JSONArray rulesArray = (JSONArray) name_.get("rules");
                        for (Object o : rulesArray) {
                            JSONObject rule = (JSONObject) o;
                            String action = (String) rule.get("action");
                            String osName = "empty";
                            if (rule.get("os") != null) osName = (String) ((JSONObject) rule.get("os")).get("name");
                            if (action.equals("allow")) {
                                if (!osName.equals("empty") && !osName.equals("windows")) {
                                    continue outer;
                                }
                            } else {
                                if (osName.equals("windows")) {
                                    continue outer;
                                }
                            }
                        }
                    }
                }
                if (os.toLowerCase().equals("linux")) {
                    if (name_.get("rules") != null) {
                        JSONArray rulesArray = (JSONArray) name_.get("rules");
                        for (Object o : rulesArray) {
                            JSONObject rule = (JSONObject) o;
                            String action = (String) rule.get("action");
                            String osName = "empty";
                            if (rule.get("os") != null) osName = (String) ((JSONObject) rule.get("os")).get("name");
                            if (action.equals("allow")) {
                                if (!osName.equals("empty") && !osName.equals("linux")) {
                                    continue outer;
                                }
                            } else {
                                if (osName.equals("linux")) {
                                    continue outer;
                                }
                            }
                        }
                    }
                }
                if (os.toLowerCase().equals("mac")) {
                    if (name_.get("rules") != null) {
                        JSONArray rulesArray = (JSONArray) name_.get("rules");
                        for (Object o : rulesArray) {
                            JSONObject rule = (JSONObject) o;
                            String action = (String) rule.get("action");
                            String osName = "empty";
                            if (rule.get("os") != null) osName = (String) ((JSONObject) rule.get("os")).get("name");
                            if (action.equals("allow")) {
                                if (!osName.equals("empty") && !osName.equals("osx")) {
                                    continue outer;
                                }
                            } else {
                                if (osName.equals("osx")) {
                                    continue outer;
                                }
                            }
                        }
                    }
                }
                JSONObject downloads = (JSONObject) name_.get("downloads");
                if (downloads.get("artifact") != null) {
                    JSONObject artifact = (JSONObject) downloads.get("artifact");
                    if (artifact.get("path") != null) {
                        String _path = (String) artifact.get("path");
                        version_path_list.add(_path);
                    }
                }
            }
        } catch (IOException | ParseException e) {
            //System.out.print(e);
        }

    }

    public void readJson_libraries_downloads_classifiers_natives_hash(String path, String natives_OS) {

        try {
            if (natives_OS.equals("Linux")) {
                natives_OS = natives_OS.replace("Linux", "natives-linux");
            } else if (natives_OS.equals("Windows")) {
                natives_OS = natives_OS.replace("Windows", "natives-windows");
            } else if (natives_OS.equals("Mac")) {
                natives_OS = natives_OS.replace("Mac", "natives-osx");
            } else {
                System.out.print("N/A");
                //I DON'T KNOW THIS OS!
            }
            String content = new Scanner(new File(path)).useDelimiter("\\Z").next();
            //System.out.println(content);
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
            try {

                String script_js = "var getJsonLibrariesDownloadsClassifiersNativesHash=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].sha1+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesY=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].path+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesZ=function(r){var s=r,a=JSON.parse(s),e=\"\",n=0;for(i=0;i<500;i++)try{a.libraries[n].natives?(e=e+a.libraries[n].name+\"\\n\",n+=1):n+=1}catch(t){n+=1}return e};";

                File file = new File("./.script.js");
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(script_js);
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            engine.eval(new FileReader("./.script.js"));

            Invocable invocable = (Invocable) engine;

            Object result = invocable.invokeFunction("getJsonLibrariesDownloadsClassifiersNativesHash", content, natives_OS);

            for (String retval : result.toString().split("\n")) {
                version_hash_list_natives.add(retval);
            }
            new File("./.script.js").delete();
        } catch (FileNotFoundException | ScriptException | NoSuchMethodException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void readJson_libraries_downloads_classifiers_natives_size(String path, String natives_OS) {

        try {
            if (natives_OS.equals("Linux")) {
                natives_OS = natives_OS.replace("Linux", "natives-linux");
            } else if (natives_OS.equals("Windows")) {
                natives_OS = natives_OS.replace("Windows", "natives-windows");
            } else if (natives_OS.equals("Mac")) {
                natives_OS = natives_OS.replace("Mac", "natives-osx");
            } else {
                System.out.print("N/A");
                //I DON'T KNOW THIS OS!
            }
            String content = new Scanner(new File(path)).useDelimiter("\\Z").next();
            //System.out.println(content);
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
            try {

                String script_js = "var getJsonLibrariesDownloadsClassifiersNativesX=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].size+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesY=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].path+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesZ=function(r){var s=r,a=JSON.parse(s),e=\"\",n=0;for(i=0;i<500;i++)try{a.libraries[n].natives?(e=e+a.libraries[n].name+\"\\n\",n+=1):n+=1}catch(t){n+=1}return e};";

                File file = new File("./.script.js");
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(script_js);
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            engine.eval(new FileReader("./.script.js"));

            Invocable invocable = (Invocable) engine;

            Object result = invocable.invokeFunction("getJsonLibrariesDownloadsClassifiersNativesX", content, natives_OS);

            for (String retval : result.toString().split("\n")) {
                version_size_list_natives.add(Long.valueOf(retval));
            }
            new File("./.script.js").delete();
        } catch (FileNotFoundException | ScriptException | NoSuchMethodException ex) {
            System.out.println(ex.getMessage());
        }
    }

    //edit this function to add more operating systems
    public void readJson_libraries_downloads_classifiers_natives_X(String path, String natives_OS) {

        try {
            if (natives_OS.equals("Linux")) {
                natives_OS = natives_OS.replace("Linux", "natives-linux");
            } else if (natives_OS.equals("Windows")) {
                natives_OS = natives_OS.replace("Windows", "natives-windows");
            } else if (natives_OS.equals("Mac")) {
                natives_OS = natives_OS.replace("Mac", "natives-osx");
            } else {
                System.out.print("N/A");
                //I DON'T KNOW THIS OS!
            }
            String content = new Scanner(new File(path)).useDelimiter("\\Z").next();
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
            try {

                String script_js = "var getJsonLibrariesDownloadsClassifiersNativesX=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].url+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesY=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].path+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesZ=function(r){var s=r,a=JSON.parse(s),e=\"\",n=0;for(i=0;i<500;i++)try{a.libraries[n].natives?(e=e+a.libraries[n].name+\"\\n\",n+=1):n+=1}catch(t){n+=1}return e};";

                File file = new File("./.script.js");
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(script_js);
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            engine.eval(new FileReader("./.script.js"));

            Invocable invocable = (Invocable) engine;

            Object result = invocable.invokeFunction("getJsonLibrariesDownloadsClassifiersNativesX", content, natives_OS);

            for (String retval : result.toString().split("\n")) {
                version_url_list_natives.add(retval);
            }
            new File("./.script.js").delete();
        } catch (FileNotFoundException | ScriptException | NoSuchMethodException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void readJson_libraries_downloads_classifiers_natives_Y(String path, String natives_OS) {

        try {
            if (natives_OS.equals("Linux")) {
                natives_OS = natives_OS.replace("Linux", "natives-linux");
            } else if (natives_OS.equals("Windows")) {
                natives_OS = natives_OS.replace("Windows", "natives-windows");
            } else if (natives_OS.equals("Mac")) {
                natives_OS = natives_OS.replace("Mac", "natives-osx");
            } else {
                System.out.print("N/A");
                //I DON'T KNOW THIS OS!
            }
            String content = new Scanner(new File(path)).useDelimiter("\\Z").next();
            //System.out.println(content);
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
            try {

                String script_js = "var getJsonLibrariesDownloadsClassifiersNativesX=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].url+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesY=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].path+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesZ=function(r){var s=r,a=JSON.parse(s),e=\"\",n=0;for(i=0;i<500;i++)try{a.libraries[n].natives?(e=e+a.libraries[n].name+\"\\n\",n+=1):n+=1}catch(t){n+=1}return e};";

                File file = new File("./.script.js");
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(script_js);
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            engine.eval(new FileReader("./.script.js"));

            Invocable invocable = (Invocable) engine;

            Object result = invocable.invokeFunction("getJsonLibrariesDownloadsClassifiersNativesY", content, natives_OS);

            for (String retval : result.toString().split("\n")) {
                version_path_list_natives.add(retval);
            }
        } catch (FileNotFoundException | ScriptException | NoSuchMethodException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void readJson_libraries_downloads_classifiers_natives_Z(String path) {

        try {

            String content = new Scanner(new File(path)).useDelimiter("\\Z").next();
            //System.out.println(content);
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
            try {

                String script_js = "var getJsonLibrariesDownloadsClassifiersNativesX=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].url+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesY=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].path+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesZ=function(r){var s=r,a=JSON.parse(s),e=\"\",n=0;for(i=0;i<500;i++)try{a.libraries[n].natives?(e=e+a.libraries[n].name+\"\\n\",n+=1):n+=1}catch(t){n+=1}return e};";

                File file = new File("./.script.js");
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(script_js);
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            engine.eval(new FileReader("./.script.js"));

            Invocable invocable = (Invocable) engine;

            Object result = invocable.invokeFunction("getJsonLibrariesDownloadsClassifiersNativesZ", content);

            for (String retval : result.toString().split("\n")) {
                version_name_list_natives.add(retval);
            }
        } catch (FileNotFoundException | ScriptException | NoSuchMethodException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void readJson_twitch_natives_Windows(String path) {
        try {
            boolean is64bit = System.getProperty("sun.arch.data.model").contains("64");
            String natives_OS = "natives-windows-" + (is64bit ? "64" : "32");
            String content = new Scanner(new File(path)).useDelimiter("\\Z").next();
            //System.out.println(content);
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
            try {

                String script_js = "var getJsonLibrariesDownloadsClassifiersNativesX=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].url+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesY=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].path+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesZ=function(r){var s=r,a=JSON.parse(s),e=\"\",n=0;for(i=0;i<500;i++)try{a.libraries[n].natives?(e=e+a.libraries[n].name+\"\\n\",n+=1):n+=1}catch(t){n+=1}return e};";

                File file = new File("./.script.js");
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(script_js);
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            engine.eval(new FileReader("./.script.js"));

            Invocable invocable = (Invocable) engine;

            Object result = invocable.invokeFunction("getJsonLibrariesDownloadsClassifiersNativesY", content, natives_OS);

            for (String retval : result.toString().split("\n")) {
                if (!retval.isEmpty()) version_path_list_natives.add(retval);
            }
        } catch (FileNotFoundException | ScriptException | NoSuchMethodException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void readJson_twitch_natives_size_Windows(String path) {

        try {
            boolean is64bit = System.getProperty("sun.arch.data.model").contains("64");
            String natives_OS = "natives-windows-" + (is64bit ? "64" : "32");
            String content = new Scanner(new File(path)).useDelimiter("\\Z").next();
            //System.out.println(content);
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
            try {

                String script_js = "var getJsonLibrariesDownloadsClassifiersNativesX=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].size+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesY=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].path+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesZ=function(r){var s=r,a=JSON.parse(s),e=\"\",n=0;for(i=0;i<500;i++)try{a.libraries[n].natives?(e=e+a.libraries[n].name+\"\\n\",n+=1):n+=1}catch(t){n+=1}return e};";

                File file = new File("./.script.js");
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(script_js);
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            engine.eval(new FileReader("./.script.js"));

            Invocable invocable = (Invocable) engine;

            Object result = invocable.invokeFunction("getJsonLibrariesDownloadsClassifiersNativesX", content, natives_OS);

            for (String retval : result.toString().split("\n")) {
                if (!retval.isEmpty()) version_size_list_natives.add(Long.valueOf(retval));
            }
            new File("./.script.js").delete();
        } catch (FileNotFoundException | ScriptException | NoSuchMethodException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void readJson_twitch_natives_hash_Windows(String path) {

        try {
            boolean is64bit = System.getProperty("sun.arch.data.model").contains("64");
            String natives_OS = "natives-windows-" + (is64bit ? "64" : "32");
            String content = new Scanner(new File(path)).useDelimiter("\\Z").next();
            //System.out.println(content);
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
            try {

                String script_js = "var getJsonLibrariesDownloadsClassifiersNativesX=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].sha1+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesY=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].path+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesZ=function(r){var s=r,a=JSON.parse(s),e=\"\",n=0;for(i=0;i<500;i++)try{a.libraries[n].natives?(e=e+a.libraries[n].name+\"\\n\",n+=1):n+=1}catch(t){n+=1}return e};";

                File file = new File("./.script.js");
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(script_js);
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            engine.eval(new FileReader("./.script.js"));

            Invocable invocable = (Invocable) engine;

            Object result = invocable.invokeFunction("getJsonLibrariesDownloadsClassifiersNativesX", content, natives_OS);

            for (String retval : result.toString().split("\n")) {
                if (!retval.isEmpty()) version_hash_list_natives.add(retval);
            }
            new File("./.script.js").delete();
        } catch (FileNotFoundException | ScriptException | NoSuchMethodException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void readJson_twitch_natives_url_Windows(String path) {

        try {
            boolean is64bit = System.getProperty("sun.arch.data.model").contains("64");
            String natives_OS = "natives-windows-" + (is64bit ? "64" : "32");
            String content = new Scanner(new File(path)).useDelimiter("\\Z").next();
            //System.out.println(content);
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
            try {

                String script_js = "var getJsonLibrariesDownloadsClassifiersNativesX=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].url+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesY=function(r,s){var a=r,e=JSON.parse(a),n=\"\",t=0;for(i=0;i<500;i++)try{n=n+e.libraries[t].downloads.classifiers[s].path+\"\\n\",t+=1}catch(o){t+=1}return n},getJsonLibrariesDownloadsClassifiersNativesZ=function(r){var s=r,a=JSON.parse(s),e=\"\",n=0;for(i=0;i<500;i++)try{a.libraries[n].natives?(e=e+a.libraries[n].name+\"\\n\",n+=1):n+=1}catch(t){n+=1}return e};";

                File file = new File("./.script.js");
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(script_js);
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            engine.eval(new FileReader("./.script.js"));

            Invocable invocable = (Invocable) engine;

            Object result = invocable.invokeFunction("getJsonLibrariesDownloadsClassifiersNativesX", content, natives_OS);

            for (String retval : result.toString().split("\n")) {
                if (!retval.isEmpty()) version_url_list_natives.add(retval);
            }
            new File("./.script.js").delete();
        } catch (FileNotFoundException | ScriptException | NoSuchMethodException ex) {
            System.out.println(ex.getMessage());
        }
    }


    public void readJson_objects_KEY(String path) {
        JSONParser readMCJSONFiles = new JSONParser();
        try {
            Object jsonfile;

            jsonfile = readMCJSONFiles.parse(new FileReader(path));

            JSONObject jsonObject = (JSONObject) jsonfile;
            JSONObject objects = (JSONObject) jsonObject.get("objects");

            Set fileCheckObjects = objects.keySet();
            Iterator a = fileCheckObjects.iterator();
            while (a.hasNext()) {
                String fileName = (String) a.next();
                objects_KEY.add(fileName);
            }
        } catch (FileNotFoundException exception) {
            System.out.println(exception);
        } catch (IOException | ParseException ex) {
            System.out.println(ex);
        }

    }

    public void readJson_objects_KEY_hash(String path) {
        JSONParser readMCJSONFiles = new JSONParser();
        try {
            Object jsonfile;

            jsonfile = readMCJSONFiles.parse(new FileReader(path));

            JSONObject jsonObject = (JSONObject) jsonfile;
            JSONObject objects = (JSONObject) jsonObject.get("objects");

            Set fileCheckObjects = objects.keySet();
            Iterator a = fileCheckObjects.iterator();
            while (a.hasNext()) {
                String fileName = (String) a.next();

                JSONObject fileNameObject = (JSONObject) objects.get(fileName);
                String fileHash = (String) fileNameObject.get("hash");
                objects_hash.add(fileHash);
            }
        } catch (FileNotFoundException exception) {
            System.out.println(exception);
        } catch (IOException | ParseException ex) {
            System.out.println(ex);
        }

    }

    public String readJson_downloads_client_url(String path) {
        try {

            FileReader reader = new FileReader(path);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            JSONObject downloads = (JSONObject) jsonObject.get("downloads");
            JSONObject client = (JSONObject) downloads.get("client");
            return ((String) (client.get("url")));

        } catch (FileNotFoundException exception) {
            System.out.println(exception);
        } catch (IOException | ParseException ex) {
            System.out.println(ex);
        }
        return "N/A";
    }


    public String readJson_assetIndex_url(String path) {
        try {
            FileReader reader = new FileReader(path);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            JSONObject structure = (JSONObject) jsonObject.get("assetIndex");
            return (String) (structure.get("url"));

        } catch (IOException | ParseException e) {
            System.out.print(e);
        }
        return "N/A";
    }

    public String readJson_assetIndex_id(String path) {
        try {
            FileReader reader = new FileReader(path);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            JSONObject structure = (JSONObject) jsonObject.get("assetIndex");
            return (String) (structure.get("id"));

        } catch (IOException | ParseException e) {
            System.out.print(e);
        }
        return "N/A";
    }

    public String readJson_minecraftArguments_v2(String path) {
        try {
            FileReader reader = new FileReader(path);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            JSONObject jsonArgs = (JSONObject) jsonObject.get("arguments");
            String args = jsonArgs.get("game").toString();
            args = args.replaceAll("\\[","").replaceAll("\\]","").replaceAll(",", "").replaceAll("\"\"", " ").replaceAll("\"", "");
            String[] argsF = args.split("\\{rules");
            return (argsF[0]);

        } catch (IOException | ParseException e) {
            System.out.print(e);
        }
        return "N/A";
    }

    public String readJson_minecraftArguments(String path) {
        try {
            FileReader reader = new FileReader(path);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            return (String) (jsonObject.get("minecraftArguments"));

        } catch (IOException | ParseException e) {
            System.out.print(e);
        }
        return "N/A";
    }

    public String readJson_assets(String path) {
        try {
            FileReader reader = new FileReader(path);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            return (String) (jsonObject.get("assets"));

        } catch (IOException | ParseException e) {
            System.out.print(e);
        }
        return "N/A";
    }

    public String readJson_id(String path) {
        try {
            FileReader reader = new FileReader(path);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            return (String) (jsonObject.get("id"));

        } catch (IOException | ParseException e) {
            System.out.print(e);
        }
        return "N/A";
    }

    public String readJson_mainClass(String path) {
        try {
            FileReader reader = new FileReader(path);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            return (String) (jsonObject.get("mainClass"));

        } catch (IOException | ParseException e) {
            System.out.print(e);
        }
        return "N/A";
    }

    public String[] generateMinecraftArguments(String OS, String auth_player_name, String version_name, String game_directory, String assets_root, String assets_index_name, String auth_uuid, String auth_access_token, String user_properties, String user_type, String version_type, String game_assets, String auth_session) {
        Local local = new Local();
        Utils utils = new Utils();

        String cmdArgs = local.readJson_minecraftArguments(utils.getMineCraft_Versions_X_X_json(OS, version_name));
        if (cmdArgs==null)
        {
            //run v2
            cmdArgs = local.readJson_minecraftArguments_v2(utils.getMineCraft_Versions_X_X_json(OS, version_name));
        }

        //the arguments can start with -- or $
        cmdArgs = cmdArgs.replaceAll(" +", " ");
        //the above will change it to single space.
        //split it to String and move it to ArrayList
        String[] tempArgsSplit = cmdArgs.split(" ");
        for (int i = 0; i < tempArgsSplit.length; i++) {
            if (tempArgsSplit[i].equals("${auth_player_name}")) {
                tempArgsSplit[i] = auth_player_name;
            }
            if (tempArgsSplit[i].equals("${version_name}")) {
                tempArgsSplit[i] = version_name;
            }
            if (tempArgsSplit[i].equals("${game_directory}")) {
                tempArgsSplit[i] = game_directory;
            }
            if (tempArgsSplit[i].equals("${assets_root}")) {
                tempArgsSplit[i] = assets_root;
            }
            if (tempArgsSplit[i].equals("${assets_index_name}")) {
                tempArgsSplit[i] = assets_index_name;
            }
            if (tempArgsSplit[i].equals("${auth_uuid}")) {
                tempArgsSplit[i] = auth_uuid;
            }
            if (tempArgsSplit[i].equals("${auth_access_token}")) {
                tempArgsSplit[i] = auth_access_token;
            }
            if (tempArgsSplit[i].equals("${user_properties}")) {
                tempArgsSplit[i] = user_properties;
            }
            if (tempArgsSplit[i].equals("${user_type}")) {
                tempArgsSplit[i] = user_type;
            }
            if (tempArgsSplit[i].equals("${version_type}")) {
                tempArgsSplit[i] = version_type;
            }
            if (tempArgsSplit[i].equals("${game_assets}")) {
                tempArgsSplit[i] = game_assets;
            }
            if (tempArgsSplit[i].equals("${auth_session}")) {
                tempArgsSplit[i] = auth_session;
            }
        }
        return tempArgsSplit;
    }

    boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        }catch (NumberFormatException e) {
            return false;
        }
    }

    public String generateLibrariesArguments(String OS) {
        String cp = "";
        Utils utils = new Utils();

        List list = new ArrayList<String>();
        list.addAll(libraries_path);

        List removeList = new ArrayList<String>();

//        Collections.sort(list, (a, b)-> {
//            if (a == null || b == null) return 0;
//            File aFile = new File((String) a);
//            File bFile = new File((String) b);
//            String aname = aFile.getName();
//            String bname = bFile.getName();
//            if (!aname.contains("lwjgl") || !bname.contains("lwjgl")) return 0;
//            String aremoved = aname.substring(0, aname.lastIndexOf('.'));
//            String bremoved = bname.substring(0, bname.lastIndexOf('.'));
//            for (String str : aremoved.split("-")) {
//                if (isInteger(str)) {
//                    if (Integer.parseInt(str) > 1000) {
//                        aremoved = aremoved.replaceAll("-" + str, "");
//                    }
//                }
//            }
//            for (String str : bremoved.split("-")) {
//                if (isInteger(str)) {
//                    if (Integer.parseInt(str) > 1000) {
//                        bremoved = bremoved.replaceAll("-" + str, "");
//                    }
//                }
//            }
//            if (aremoved.replaceAll("[\\D]", "").isEmpty()) return 0;
//            if (bremoved.replaceAll("[\\D]", "").isEmpty()) return 0;
//            int versiona = Integer.parseInt(aremoved.replaceAll("[\\D]", ""));
//            int versionB = Integer.parseInt(bremoved.replaceAll("[\\D]", ""));
//            String formattedvera = aremoved.replace("_", "").replace("-", "").replaceAll("[A-Za-z]?", "");
//            String formattedverb = bremoved.replace("_", "").replace("-", "").replaceAll("[A-Za-z]?", "");
//            if (!aname.replaceAll(formattedvera, "").equals(bname.replaceAll(formattedverb, ""))) return 0;
//            if (versiona == versionB) return 0;
//            if (versiona > versionB){
//                if (!removeList.contains(b)) {
//                    removeList.add(b);
//                }
//                return 1;
//            }
//            if (versiona < versionB) {
//                if (!removeList.contains(a)) removeList.add(a);
//                return -1;
//            }
//            return 0;
//        });

        List sorted = new ArrayList();
        sorted.addAll(list);
        //sorted.removeAll(removeList);

        for (int i = 0; i < sorted.size(); i++) {

            if (cp.contains(sorted.get(i).toString())) continue;

            if (i == sorted.size() - 1) {

                cp = cp + sorted.get(i);

            } else {
                cp = cp + sorted.get(i) + utils.getArgsDiv(OS);

            }
        }
        return cp;
    }

    public String generateRunnableArguments(String Memory, String NativesDir, String FullLibraryArgument, String mainClass, String HalfArgument) {
        //unused function. Will be removed
        return ("-Xmx" + Memory + " -XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump -Djava.library.path=" + NativesDir + " -cp " + FullLibraryArgument + " " + mainClass + " " + HalfArgument);

    }

    public String generateRunnableArguments(String Memory, String MinMemory, String NativesDir, String FullLibraryArgument, String mainClass, String HalfArgument) {
        //unused function. Will be removed
        return ("-Xms" + Memory + " -Xmx" + Memory + " -XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump -Djava.library.path=" + NativesDir + " -cp " + FullLibraryArgument + " " + mainClass + " " + HalfArgument);

    }

    public String generateLibrariesPath(String _OS, String _name) {
        try {
            String fileName = _name;
            String[] colonSplit = fileName.split("\\:", 3);
            String[] folderSplit = colonSplit[0].split("\\.");

            String compileSplit = "";

            String compileFolder = "";

            for (int i = 0; i < folderSplit.length; i++) {
                compileFolder += folderSplit[i] + "/";
            }
            compileSplit = compileFolder + "/" + colonSplit[1] + "/" + colonSplit[2] + "/" + colonSplit[1] + "-" + colonSplit[2] + ".jar";
            compileSplit = compileSplit.replace("//", "/");
            /*
                Downloading: https://libraries.minecraft.net/org/ow2/asm/asm-all/4.1/asm-all-4.1.jar
                org/ow2/asm/asm-all/4.1/asm-all-4.1.jar

             */
            //compileSplit = utils.getMineCraftLibrariesLocation(_OS) + "/" + compileSplit;
            return (compileSplit);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    public String getNatives_OS(String natives_OS) {
        try {
            if (natives_OS.equals("Linux")) {
                return natives_OS.replace("Linux", "natives-linux");
            } else if (natives_OS.equals("Windows")) {
                return natives_OS.replace("Windows", "natives-windows");
            } else if (natives_OS.equals("Mac")) {
                return natives_OS.replace("Mac", "natives-osx");
            } else {
                return "N/A";
                //I DON'T KNOW THIS OS!
            }
        }catch (Exception ex) {
            System.out.println(ex.toString());
            return "N/A";
        }
    }

    public String generateNativesPath(String natives_OS, String _name) {
        try {
            if (natives_OS.equals("Linux")) {
                natives_OS = natives_OS.replace("Linux", "natives-linux");
            } else if (natives_OS.equals("Windows")) {
                natives_OS = natives_OS.replace("Windows", "natives-windows");
            } else if (natives_OS.equals("Mac")) {
                natives_OS = natives_OS.replace("Mac", "natives-osx");
            } else {
                System.out.print("N/A");
                //I DON'T KNOW THIS OS!
            }
            String fileName = _name;
            String[] colonSplit = fileName.split("\\:", 3);
            String[] folderSplit = colonSplit[0].split("\\.");

            String compileSplit = "";

            String compileFolder = "";

            for (int i = 0; i < folderSplit.length; i++) {
                compileFolder += folderSplit[i] + "/";
            }

            compileSplit = compileFolder + "/" + colonSplit[1] + "/" + colonSplit[2] + "/" + colonSplit[1] + "-" + colonSplit[2] + "-" + natives_OS + ".jar";
            compileSplit = compileSplit.replace("//", "/");
            return (compileSplit);

        } catch (Exception e) {
            System.out.println(e);
        }
        return "N/A";
    }

    public Boolean checkIfVanillaMC(String version) {
        for (int i = 0; i < version_manifest_versions_id.size(); i++) {
            if (version_manifest_versions_id.get(i).equals(version)) {
                return true;
            }
        }
        //if nothing.. return false
        return false;
    }

    public String readJson_inheritsFrom(String path) {
        try {
            FileReader reader = new FileReader(path);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            return (String) (jsonObject.get("inheritsFrom"));

        } catch (IOException | ParseException e) {
            System.out.print(e);
        }
        return "N/A";
    }

    public String readJson_jar(String path) {
        try {
            FileReader reader = new FileReader(path);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            return (String) (jsonObject.get("jar"));

        } catch (IOException | ParseException e) {
            System.out.print(e);
        }
        return "N/A";
    }
}