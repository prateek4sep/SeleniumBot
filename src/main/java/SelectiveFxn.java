import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SelectiveFxn {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method m;
        String name = "m1";
        String param = "x";
        if(param!=null) {
            m = SelectiveFxn.class.getMethod(name, (new String()).getClass());
            m.invoke(null, new String(param));
        }
        else {
            m = SelectiveFxn.class.getMethod(name, new Class[]{});
            m.invoke(null, new Object[]{});
        }
    }

    public static void m1(String test){
        System.out.println("m1 hoon main" + test);
    }

    public static void m2(){
        System.out.println("m2 hoon main");
    }
}
