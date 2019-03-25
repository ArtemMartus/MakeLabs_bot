package maincode;

import maincode.controllers.MakeLabs_bot;
import maincode.data.DataClass;
import maincode.helper.Log;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

public class Main {

    public static void main(String[] args) throws Exception {
        /*

        HashMap<String,Integer> buttons = new HashMap<>();
        buttons.put("Мои заказы",-1);
        buttons.put("Сделать заказ", -1);
        buttons.put("О нас", -1);
        buttons.put("Сотрудничество", -1);

        PostWorkDataGenerator.generateForm("/", "Домашняя страница" +
                "\nЗдесь вы можете посмотреть статус, сделать новый заказ" +
                "\nИ связаться с нами",buttons);

        buttons = new HashMap<>();
        buttons.put("Назад",-1);
        PostWorkDataGenerator.generateForm("/make", "Выберете предмет:",buttons);

        buttons = new HashMap<>();
        buttons.put("Назад",-1);
        PostWorkDataGenerator.generateForm("/make", "Выберете предмет:",buttons);
*/
/*


        PostWorkController.loadWork();

        PostWorkData maincode.maincode.data = PostWorkController.getData("/");
        List<PostWorkData> data_children = PostWorkController.getChildren("/");


        System.out.println(maincode.maincode.data.getIURI()+" - " + maincode.maincode.data.getDescription());
        for (PostWorkData child : data_children) {
            System.out.println("\t" + child.getIURI() + " - " + child.getDescription()+" ["+child.getFileName()+"]");
            if(child.hasParams())
                for (Pair<String,Integer> pair: child.getParams())
                    System.out.println("\t\t" + child.getIURI()+"/"+pair.getFirst() + " - " + pair.getSecond());
        }

        maincode.maincode.data = PostWorkController.getData(data_children.get(0).getIURI());
        data_children = PostWorkController.getChildren(maincode.maincode.data.getIURI());


        System.out.println(maincode.maincode.data.getIURI()+" - " + maincode.maincode.data.getDescription());
        for (PostWorkData child : data_children) {
            System.out.println("\t" + child.getIURI() + " - " + child.getDescription()+" ["+child.getFileName()+"]");
            if(child.hasParams())
                for (Pair<String,Integer> pair: child.getParams())
                    System.out.println("\t\t" + child.getIURI()+"/"+pair.getFirst() + " - " + pair.getSecond());
        }

*/


        Log.setShowLevel(Log.DEBUG);
        ApiContextInitializer.init();
        DataClass dataClass = new DataClass();
        MakeLabs_bot bot = new MakeLabs_bot(dataClass);
        TelegramBotsApi api = new TelegramBotsApi();
        try {
            api.registerBot(bot);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }
}
