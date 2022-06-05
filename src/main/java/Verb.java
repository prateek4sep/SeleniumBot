import java.util.List;

public class Verb {
    public String type[] = {"enter","input","type"};
    public String validate[] = {"verify","validate","sure","should","contain"};
    public String click[] = {"click"};
    public String navigate[] = {"go to","goto","navigate","launch"};

    public String getVerb(List<String> lms){
        for(String t:type){
            if(lms.contains(t)) return "type";
        }
        for(String t:validate){
            if(lms.contains(t)) return "validate";
        }
        for(String t:click){
            if(lms.contains(t)) return "click";
        }
        for(String t:navigate){
            if(lms.contains(t)) return "navigate";
        }
        return null;
    }
}
