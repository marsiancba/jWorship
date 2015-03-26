/*
 * Created on 30.4.2004
 * Modified on 2015/02/10
 */
package sk.calvary.misc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author marsian
 * 
 */
public class Lang {
    private boolean changed = false;

    /**
     * Komentar na zaciatku
     */
    String introComment = "";

    /**
     * stringy
     */
    Vector<LangString> strings = new Vector<LangString>();
    Vector<String> langs = new Vector<String>();

    File filename = null;

    class LangString {
        LangString(String key) {
            this.key = key;
        }

        String key;
        String introComment = "";
        String link = null;
        Hashtable<String, String> strings = new Hashtable<String, String>();

        public boolean isLink() {
            return link != null;
        }

        public boolean hasLang(String lang) {
            return strings.containsKey(lang);
        }

        private void dieIfLink() {
            if (isLink())
                throw new UnsupportedOperationException();
        }

        public void setLink(String link) {
            if (!isLink())
                throw new NullPointerException();
            if (!strings.isEmpty())
                throw new UnsupportedOperationException();
            
            this.link = link;
        }

        /**
         * @param lang
         * @param text
         */
        public void set(String lang, String text) {
            dieIfLink();
            
            strings.put(lang, text);
            
            if (!langs.contains(lang))
                langs.add(lang);
            
            changed();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return key;
        }

        public String toHTML() {
            StringBuffer sb = new StringBuffer();
            sb.append("<h1>" + key + "</h1>");
            Object[] langs = getLangs();
            
            for (int i = 0; i < langs.length; i++) {
                String l = (String) langs[i];
                sb.append("<h2>" + l + "</h2>");
                sb.append(get(l));
            }
            
            return sb.toString();
        }

        /**
         * @param l
         * @return
         */
        public String get(String lang) {
            dieIfLink();
            
            String string = strings.get(lang);
            
            return (string == null) ? "" : string;
        }

        public String getWithAlternative(String lang) {
        	dieIfLink();
        	
        	String s = get(lang);
        	
        	if (s.equals(""))
        		s = get(defaultReplaceLang(lang));
        	
        	return s;
        }
        
        public String getWithLink(String lang) {
            if (isLink())
                return Lang.this.get(link).getWithLink(lang);
            else
                return get(lang);
        }

        /**
         * @return Returns the key.
         */
        public String getKey() {
            return key;
        }

        /**
         * @param origLang
         * @param newLang
         */
        public void renameLang(String origLang, String newLang) {
            if (!isLink()){
            	String s = strings.get(origLang);
            	if (s != null) {
            		strings.remove(origLang);
            		strings.put(newLang, s);
            		changed();
            	}
            }
        }

        public boolean isEmpty() {
            for (String s : strings.values()) {
                if (s.length() > 0)
                    return false;
            }
            return true;
        }

        Set<String> getWords(String lang) {
            HashSet<String> res = new HashSet<String>();
            StringTokenizer t = new StringTokenizer(get(lang),
                    " 0123456789\r\n\t.,;:?\\/\"'{}|()%-+*<>");
            
            while (t.hasMoreTokens()) {
                res.add(t.nextToken().toLowerCase());
            }
            
            return res;
        }
    }

    public static Lang parse(InputStream in) throws IOException {
    	BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
        Lang langObj = new Lang();
        LangString string = null;
        String nextComment = null;
        
        String line = buffer.readLine();
        
        if (line == null)
        	throw new FileNotFoundException("Lang file is empty!");
        
    	if (line.startsWith("\u00ef\u00bb\u00bf")) {
    		line = line.substring(3);
    	}
        
        while (line != null) {
            line = line.trim();
            
            if (line.equals("")) {
                if (nextComment == null)
                    nextComment = "";
            }
            else if (line.startsWith("//")) {
            	// komentar na zaciatku
                if (nextComment != null) {
                    nextComment += line + "\n";
                } else {
                    langObj.introComment += line + "\n";
                }
            }
            else if (line.startsWith("#")) {
            	// zaciatok stringu
                string = langObj.new LangString(line);
                
                if (nextComment != null) {
                    string.introComment = nextComment;
                    nextComment = "";
                }
                
                langObj.strings.add(string);
            }
            else if (line.startsWith("-> ")) {
            	// linka
                String link = line.substring(3).trim();
                
                string.setLink(link);
            }
            
            if (line.indexOf('[') >= 0) {
            	// string
                int i1 = line.indexOf('[');
                int i2 = line.lastIndexOf(']');
                if (i2 < 0)
                    i2 = line.length();
                
                String lang = line.substring(0, i1).trim();
                String text = line.substring(i1 + 1, i2);
                
                text = text.replace("\\n", "\r\n");
                
                if (text.length() > 0 || !string.hasLang(lang))
                	string.set(lang, text);
            }
            
            line = buffer.readLine();
        }
        
        langObj.setChanged(false);
        
        return langObj;
    }

    public void save(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        
        dos.writeBytes("\u00ef\u00bb\u00bf");
        dos.writeBytes(getIntroComment());
        dos.writeBytes("\r\n");
        
        String langs[] = getLangs();
        LangString strings[] = getStrings();
        
        for (int i = 0; i < strings.length; i++) {
            LangString s = strings[i];
            if (!s.introComment.equals("")) {
                dos.writeBytes(s.introComment);
                dos.writeBytes("\r\n");
            }
            
            dos.writeBytes(s.getKey() + "\r\n");
            
            if (s.isLink()) {
                dos.writeBytes(" -> ");
                dos.writeBytes(s.link);
                dos.writeBytes("\r\n");
            } else {
                boolean empty = s.isEmpty();
                
                for (String lang : langs) {
                    if (!empty || s.hasLang(lang)){
                    	String b = s.get(lang);
                    	
                    	b = b.replace("\r", "");
                    	b = b.replace("\n", "\\n");
                    	
                    	dos.writeBytes(lang + " [" + b + "]\r\n");
                    }
                }
            }
            
            dos.writeBytes("\r\n");
        }
        
        os.close();
    }

    /**
     * @return Returns the introComment.
     */
    public String getIntroComment() {
        return introComment;
    }

    /**
     * @param introComment
     *            The introComment to set.
     */
    public void setIntroComment(String introComment) {
        this.introComment = introComment;
        changed();
    }

    /**
     * @return Returns the langs.
     */
    public String[] getLangs() {
        return (String[]) langs.toArray(new String[0]);
    }

    /**
     * @return Returns the strings.
     */
    public LangString[] getStrings() {
        return (LangString[]) strings.toArray(new LangString[0]);
    }

    /**
     * @return Returns the filename.
     */
    public File getFilename() {
        return filename;
    }

    /**
     * @param filename
     *            The filename to set.
     */
    public void setFilename(File filename) {
        this.filename = filename;
    }

    private LangString get(String key) {
        for (LangString langString : strings) {
            if (langString.key.equals(key))
                return langString;
        }
        
        return null;
    }

    public String getString(String key, String lang) {
    	for (LangString langString : strings) {
    		if (langString.key.equals(key))
    			return langString.get(lang);
    	}
    	
    	return key;
    }
    
    /**
     * @param key
     * @return
     */
    public LangString add(String key) {
        LangString s = get(key);
        
        if (s == null) {
            s = new LangString(key);
            strings.addElement(s);
            changed();
        }
        
        return s;
    }

    public String getNextKey() {
        int i = 1000;
        
        while (true) {
            String key = "#" + i;
            
            if (get(key) == null)
                return key;
            
            i++;
        }
    }

    /**
     * @param lang
     */
    public void addLang(String lang) {
        if (!langs.contains(lang)){
        	langs.addElement(lang);
        	changed();
        }
    }

    /**
     * @param origLang
     * @param newLang
     */
    public void renameLang(String origLang, String newLang) {
        if (!langs.contains(origLang))
            throw new IllegalArgumentException(origLang);
        
        if (!origLang.equals(newLang)){
        	langs.set(langs.indexOf(origLang), newLang);
        	changed();
        	
        	for (LangString langString : getStrings()) {
        		langString.renameLang(origLang, newLang);
        	}
        }
    }

    public String defaultReplaceLang(String lang) {
    	String replace = "en";
    	
        if (lang.equals("cz"))
            replace = "sk";
        else if (lang.equals("br"))
            replace = "pt";
        else if (lang.equals("at"))
        	replace = "de";
        
        return replace;
    }
    
    void changed() {
    	setChanged(true);
    }

    void setChanged(boolean changed) {
        this.changed = changed;
    }

    boolean isChanged() {
        return changed;
    }

    public LangString find(String lang, String text) {
        for (LangString langString : strings) {
            if (langString.get(lang).equals(text))
                return langString;
        }
        
        return null;
    }
    
    public static void copyDefaultLangFile(File target) throws IOException, URISyntaxException{
    	Lang lang = new Lang();
    	URL url = lang.getClass().getResource("lang.lng");
    	URI uri = url.toURI();
    	File source = new File(uri.getPath());
    	target.createNewFile();

    	FileTools.copyFile(source, target);
    }
}