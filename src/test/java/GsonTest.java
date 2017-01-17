import io.github.biezhi.session.Session;
import io.github.biezhi.session.util.Utils;

/**
 * Created by biezhi on 2016/12/6.
 */
public class GsonTest {

    public static void main(String[] args) {
        Session session = new Session();
        session.setId("sdsdasdasdasdadadasd");
        session.put("a", "bbb");

        System.out.println(Utils.toJSONString(session));
    }

}
