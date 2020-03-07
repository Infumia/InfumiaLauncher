package com.infumia.launcher.util;

/**
 *    Copyright 2019-2020 Infumia
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

public class Local {

    public List version_url_list = new ArrayList();

    public List version_path_list = new ArrayList();
    public List version_name_list = new ArrayList();
    public List version_hash_list = new ArrayList();


    public List version_url_list_natives = new ArrayList();
    public List version_path_list_natives = new ArrayList();
    List version_name_list_natives = new ArrayList();
    public List version_hash_list_natives = new ArrayList();

    public List libraries_path = new ArrayList();

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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            ex.printStackTrace();
        }
    }

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
            ex.printStackTrace();
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
            ex.printStackTrace();
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
            ex.printStackTrace();
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
            ex.printStackTrace();
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
            ex.printStackTrace();
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
            ex.printStackTrace();
        }
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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

    public String generateLibrariesArguments(String OS) {
        String cp = "";
        Utils utils = new Utils();

        List list = new ArrayList<String>();
        list.addAll(libraries_path);

        List sorted = new ArrayList();
        sorted.addAll(list);

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

    public String generateLibrariesPath(String _name) {
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
            ex.printStackTrace();
            return "N/A";
        }
    }
}