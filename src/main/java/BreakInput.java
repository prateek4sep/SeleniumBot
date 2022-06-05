import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
generateFunction(String testStep){
    clearTestStep of unwanted characters and spaces
    <List of Strings> lemmas = Stanford Lemmatizer -> lemmatize()
    String param = extractParameter(lemmas)
    Remove param from lemmas
    String verb = extractVerb(lemmas)
    String target = extractTargetElement(lemmas)
    For the target and verb combination find a matching entry from the functions table
    Add the parameter if required by the function
    Return the generated function
}

extractParameter(list of lemmas){
    Identify if a URL is present OR,
    A Text in single or double quotes is present
    If required but not present, ask explicitly for user input
    Return the extracted parameter
}

extractTargetElement(list of lemmas){
    <List of Strings> ngrams = generateNgrams() //All combinations of input words
    Find a match by comparing ngrams to elements in the database
    If not present, ask explicitly for user input using selection
    Return the extracted target element
}

extractVerb(list of lemmas){
    Dictionary of synonyms of 'type'
    Dictionary of synonyms of 'click
    Dictionary of synonyms of 'navigate'
    Dictionary of synonyms of 'validate'
    Find if the lemmas match any matching verb
    If not present, ask explicitly for user input using selection
    Return the extracted verb
}
 */


public class BreakInput {
    public static String getFunction(String text) throws SQLException {
        System.out.println("Starting Stanford Lemmatizer");
        //String text = "Text \'Apple\' in the Search Box should be Entered";
        text = text.replaceAll("‘","'");
        text = text.replaceAll("’","'");
        String input = textInput(text);
        System.out.println("Input: "+input);
        text = cleanString(text);
        System.out.println("Cleaned: "+text);
        System.out.println(text);
        StanfordLemmatizer slem = new StanfordLemmatizer();
        List<String> lms = (slem.lemmatize(text));
        for(String l:lms){
            if(l.contains("http") || l.contains(".com"))
                input = l;
        }
        Verb v = new Verb();
        String verb = v.getVerb(lms);

        if(verb==null || verb.equals("")){
            JDialog.setDefaultLookAndFeelDecorated(true);
            Object[] selectionValues = { "Click", "Type", "Validate", "Navigate" };
            String initialSelection = "Click";
            String selection = (String) JOptionPane.showInputDialog(null, "Please select an operation.",
                    "User Action Missing", JOptionPane.QUESTION_MESSAGE, null, selectionValues, initialSelection);
            verb = selection.toLowerCase();
        }

        if(verb.equals("navigate")){
            return "navigateTo" + "(\"" + input + "\")";
        }
        System.out.println("Verb: "+verb);
        System.out.println(verb);
        System.out.println("Lemmas: "+lms);
        Ngrams ngs = new Ngrams();
        List<String> ngrams = ngs.generateNgrams(text);
        System.out.println("Ngrams:"+ngrams);
        String str = findMatch(ngrams);

        if(str==null || str.equals("")){
            ArrayList<String> elementsList=new ArrayList<String>();
            final Connection conn = MySqlConnection.getConnection();
            ResultSet rslistEl = MySqlConnection.getElementsList(conn);
            while(rslistEl.next())
            {
                String pageName = rslistEl.getString("elementName");
                elementsList.add(pageName);
            }
            JDialog.setDefaultLookAndFeelDecorated(true);
            String[] selectionValues = new String[elementsList.size()];
            elementsList.toArray(selectionValues);
            String initialSelection = selectionValues[0];
            Object selection = JOptionPane.showInputDialog(null, "Couldn't identify the element. Please select:",
                    "Select Element", JOptionPane.QUESTION_MESSAGE, null, selectionValues, initialSelection);
            str = (String) selection;
        }

        str = str.substring(0, 1).toUpperCase() + str.substring(1);
        String function = "";
        if(verb.equals("type")){
            if(input==null || input.equals(""))
                input = JOptionPane.showInputDialog(null,"Please enter an input to type:","Input Not Found",JOptionPane.QUESTION_MESSAGE);
            function = "typeIn" + str + "(\"" + input + "\")";
        } else if(verb.equals("click")){
            function = "click" + str + "()";
        } else if(verb.equals("validate")){
            if(input==null || input.equals(""))
                input = JOptionPane.showInputDialog(null,"Please enter an input to validate:","Input Not Found",JOptionPane.QUESTION_MESSAGE);
            function = "validate" + str + "(\"" + input + "\")";
        }
        return function;
    }

    public static String findMatch(List<String> ngrams) throws SQLException {
        Connection conn = MySqlConnection.getConnection();
        ResultSet res = MySqlConnection.getElementsList(conn);
        while(res.next()){
            for(String ng: ngrams){
                if(res.getString("elementName").equalsIgnoreCase(ng)){
                    System.out.println("Element: " + res.getString("elementName"));
                    System.out.println("Matching ngram: " + ng);
                    return res.getString("elementName");
                }
            }
        }
        return null;
    }

    public static String textInput(String text){
        Pattern p = Pattern.compile("[\"\']([^\"]*)[\"\']");
        Matcher m = p.matcher(text);
        while (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public static String cleanString(String text){
        text = text.replaceAll("[\"\'].*[\"\']", "");
        text = text.replaceAll("  ", " ");
        if (text.endsWith(".")) {
            text = text.substring(0, text.length() - 1);
        }
        text = text.replaceAll("\"","");
        text = text.replaceAll("'","");
        text = text.replaceAll(",","");
        text = text.toLowerCase();
        return text;
    }
}

