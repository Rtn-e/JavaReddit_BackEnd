import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {
    private final HashMap<String, String> data;

    public User(HashMap<String, String> data) {
        this.data = data;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    String checkExist() {
        String users = DataBase.getSingleTone().getController("ClientAccounts").readFile();
        String[] split = users.split("\n");
        for (String str : split) {
            String detail[] = str.split(";");

            if (detail[1].equals(data.get("username"))) {
                return str;
            }
        }
        return "invalid";
    }

    String checkExist(String username) {
        String users = DataBase.getSingleTone().getController("ClientAccounts").readFile();
        String[] split = users.split("\n");
        for (String str : split) {
            String detail[] = str.split(";");

            if (detail[1].equals(username)) {
                return str;
            }
        }
        return "invalid";
    }

    String signUp() {
        String invalidMsg = "";
        Pattern email = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = email.matcher(data.get("email"));
        if (!matcher.find()) {
            invalidMsg += "invalid email";
        }
        String user = checkExist();
        if (!user.equals("invalid")) {
            if (invalidMsg.equals(""))
                invalidMsg += "invalid username";
            else
                invalidMsg += ";invalid username";
        }

        if (!data.get("password").matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")) {
            if (invalidMsg.equals(""))
                invalidMsg += "invalid password";
            else
                invalidMsg += ";invalid password";
        }
        if (invalidMsg.equals("")) {
            DataBase.getSingleTone().getController("ClientAccounts").writeFile(data.get("email")
                    + ";" + data.get("username") + ";" + data.get("password") + ";null" + "\n");
            return "valid";
        }
        return invalidMsg;
    }

    String signIn() {
        String alreadyPhoneNumber = checkExist();
        if (alreadyPhoneNumber.equals("invalid")) {
            return "invalid username";
        } else if (!alreadyPhoneNumber.split(";")[2].equals(data.get("password"))) {
            return "invalid password";
        }
        return "valid";
    }


    String showFeed() {
        ArrayList<String> posts = new ArrayList<>();
        ArrayList<String> finalFeed = new ArrayList<>();
        String users = DataBase.getSingleTone().getController("ClientAccounts").readFile();
        String post = DataBase.getSingleTone().getController("Post").readFile();
        String[] split = users.split("\n");
        String clientCommunity = "";
        for (String str : split) {
            String detail[] = str.split(";");
            if (detail[1].equals(data.get("username"))) {
                clientCommunity = detail[3];
            }
        }
        String detail[] = clientCommunity.split("-");
        split = post.split("\n");


        for (int i = 0; i < detail.length; i++) {
            for (int j = 0; j < split.length; j++) {
                String[] p = split[j].split(";");
                if (!p[2].equals(detail[i])) {
                    continue;
                }
                posts.add(split[j]);
            }
        }

        int max = 0;
        int index = 0;
        for (int i = 0; i < posts.size(); i++) {
            for (int j = 0; j < posts.size(); j++) {
                String[] split1 = posts.get(i).split(";");
                int s1 = Integer.parseInt(split1[0]);
                if (s1 >= max) {
                    max = s1;
                    index = j;
                }
            }
            finalFeed.add(posts.get(index));
            posts.remove(index);
            max = 0;
        }
        String finalStr = "";
        for (int i = 0; i < finalFeed.size(); i++) {
            finalStr += finalFeed.get(i) ;
            if (i+1 != finalFeed.size()) {
                finalStr += "@";
            }
        }
        return finalStr;
    }


    String addPost() {
        Date date = new Date();
        DataBase.getSingleTone().getController("Post").writeFile(Server.postNum + ";" + data.get("username") + ";" + data.get("community") + ";" +
                data.get("title") + ";" + data.get("description") + ";" + date + ";" + "0" + ";" + "0" + "\n");
        Server.postNum++;
        DataBase.getSingleTone().addDataBase("postNum", new Controller("C:\\Users\\Rtn_e\\Desktop\\Project\\DataBase\\postNum.txt"));
        DataBase.getSingleTone().getController("postNum").writeFile((Server.postNum + "\n"), true);
        return "valid";
    }

    String editUsername() {
        if (!checkExist(data.get("newName")).matches("invalid")) {
            return "invalid username";
        }
        int line = 0;
        String users = DataBase.getSingleTone().getController("ClientAccounts").readFile();
        String[] split = users.split("\n");
        StringBuilder ans = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            String detail[] = split[i].split(";");
            if (detail[1].equals(data.get("username"))) {
                line = i;
                String[] update = split[i].split(";");
                update[1] = data.get("newName");
                StringBuilder strBuilder = new StringBuilder(update[0] + ";");
                strBuilder.append(update[1] + ";");
                strBuilder.append(update[2] + ";");
                strBuilder.append(update[3]);
                split[i] = strBuilder.toString();
                ans.append(split[i]).append("\n");
                break;
            }
        }
        String finalStr = "";
        for (int i = 0; i < split.length; i++) {
            finalStr += (split[i]) + "\n";
        }
        split[line] = ans.toString();
        DataBase.getSingleTone().getController("ClientAccounts").writeFile(finalStr, true);
        return "valid";
    }

    String editEmail() {
        Pattern email = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = email.matcher(data.get("newEmail"));
        if (!matcher.find()) {
            return "invalid";
        }
        int line = 0;
        String users = DataBase.getSingleTone().getController("ClientAccounts").readFile();
        String[] split = users.split("\n");
        StringBuilder ans = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            String detail[] = split[i].split(";");
            if (detail[1].equals(data.get("username"))) {
                line = i;
                String[] update = split[i].split(";");
                update[0] = data.get("newEmail");
                StringBuilder strBuilder = new StringBuilder(update[0] + ";");
                strBuilder.append(update[1] + ";");
                strBuilder.append(update[2] + ";");
                strBuilder.append(update[3]);
                split[i] = strBuilder.toString();
                ans.append(split[i]).append("\n");
                break;
            }
        }
        String finalStr = "";
        for (int i = 0; i < split.length; i++) {
            finalStr += (split[i]) + "\n";
        }
        split[line] = ans.toString();
        DataBase.getSingleTone().getController("ClientAccounts").writeFile(finalStr, true);

        return "valid";
    }

    String editPassword() {
        String users = DataBase.getSingleTone().getController("ClientAccounts").readFile();
        String[] split = users.split("\n");
        StringBuilder ans = new StringBuilder();
        int line = 0;
        for (int i = 0; i < split.length; i++) {
            String detail[] = split[i].split(";");
            if (detail[1].equals(data.get("username"))) {
                line = i;
                if (data.get("oldPassword").equals(detail[2])) {
                    if (data.get("newPassword").matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")) {
                        if (data.get("newPassword").equals(data.get("confirmPassword"))) {
                            String[] update = split[i].split(";");
                            update[2] = data.get("newPassword");
                            StringBuilder strBuilder = new StringBuilder(update[0] + ";");
                            strBuilder.append(update[1] + ";");
                            strBuilder.append(update[2] + ";");
                            strBuilder.append(update[3]);
                            split[i] = strBuilder.toString();
                            break;
                        } else {
                            return "invalid confirmPassword";
                        }
                    } else {
                        return "invalid newPassword";
                    }
                } else {
                    return "invalid oldPassword";
                }
            }
        }
        String finalStr = "";
        for (int i = 0; i < split.length; i++) {
            finalStr += (split[i]) + "\n";
        }
        split[line] = ans.toString();
        DataBase.getSingleTone().getController("ClientAccounts").writeFile(finalStr, true);

        return "valid";
    }

    String showProfile() {
        return checkExist(data.get("username"));
    }

    String addFavPost() {
        String users = DataBase.getSingleTone().getController("FavPost").readFile();
        String[] split = users.split("\n");
        String last = "";
        for (int i = 0 ; i < split.length ; i++ ) {
            String detail[] = split[i].split(";");
            if (detail[0].equals(data.get("username"))) {
                detail[1] += "-" + data.get("postId");
                last = detail[0] + ";" + detail[1];
                split[i] = last;
                break;
            }
        }
        if (last.equals(""))
         DataBase.getSingleTone().getController("FavPost").writeFile(data.get("username") +  ";" + data.get("postId"));
        else {
            String l = "";
            for (int i = 0 ; i<split.length ; i++) {
                l += split[i];
                if (i + 1 != split.length) {
                    l+= ";";
                }
                l+= "\n";
            }
            DataBase.getSingleTone().getController("FavPost").writeFile(l, true);
        }

        return "valid";
    }

    String getFavPost() {
        String users = DataBase.getSingleTone().getController("FavPost").readFile();
        String post = DataBase.getSingleTone().getController("Post").readFile();
        String[] split = users.split("\n");
        String[] split2 = post.split("\n");
        String favPost = "";
        for (int i = 0 ; i < split.length ; i++ ) {
            String detail[] = split[i].split(";");
            if (detail[0].equals(data.get("username"))) {
                String[] f = detail[1].split("-");
                for (int j = 0; j < f.length; j++) {
                    for (int k = 0; k < split2.length; k++) {
                        String[] s = split2[k].split(";");
                        if (f[j].equals(s[0])) {
                            favPost += split2[k];
                            if (j+1 != f.length) {
                                favPost += "@";
                            }
                        }
                    }
                }
                break;
            }
        }
        if (favPost.equals(""))
            return "no favePost yet!";
        return favPost;
    }
    String addComment() {
            String finalStr = data.get("postId") + ";" + data.get("username") + ":" + data.get("comment") + ";" + "0" + ";" + "0" + "\n";
            DataBase.getSingleTone().getController("postComments").writeFile(finalStr);
        return "valid";
    }
    String getPostComment() {
        String users = DataBase.getSingleTone().getController("postComments").readFile();
        String[] split = users.split("\n");
        String finalStr = "";
        for (String str : split) {
            String[] s = str.split(";");
                if (s[0].equals(data.get("postId"))) {
                    finalStr += str + "\n";
                }
        }
        return finalStr;
    }
    String postDetail() {
        String finalStr = "";
        String post = DataBase.getSingleTone().getController("Post").readFile();
        String[] split = post.split("\n");
        for (int i = 0; i < split.length; i++) {
            String[] s = split[i].split(";");
            if (s[0].equals(data.get("postId"))) {
                finalStr += "id:" + s[0] + "\n";
                finalStr += "user:" + s[1] + "\n";
                finalStr += "community:" + s[2] + "\n";
                finalStr += "title:" + s[3] + "\n";
                finalStr += "des:" + s[4] + "\n";
                finalStr += "date:" + s[5] + "\n";
                finalStr += "like:" + s[6] + "\n";
                finalStr += "disLike:" + s[7] + "\n";
                finalStr += "comments:" + getPostComment();
            }
        }
        return finalStr;
    }
    String likePost() {
        String post = DataBase.getSingleTone().getController("Post").readFile();
        String[] split = post.split("\n");
        String finalStr = "";
        for (int i = 0; i < split.length; i++) {
            String[] s = split[i].split(";");
            if (data.get("postId").equals(s[0])) {
                s[6] = String.valueOf((Integer.parseInt(s[6]) + 1));
                split[i] = s[0] + ";" + s[1] + ";" + s[2] + ";" + s[3] + ";" + s[4] + ";" + s[5] + ";" + s[6] + ";" + s[7]  + "\n";
            }
        }
        for (int i = 0; i < split.length; i++) {
            finalStr += split[i] + "\n";
        }
        DataBase.getSingleTone().getController("Post").writeFile(finalStr);
        return "valid";
    }
    String dislikePost() {
        String post = DataBase.getSingleTone().getController("Post").readFile();
        String[] split = post.split("\n");
        String finalStr = "";
        for (int i = 0; i < split.length; i++) {
            String[] s = split[i].split(";");
            if (data.get("postId").equals(s[0])) {
                s[6] = String.valueOf((Integer.parseInt(s[7]) + 1));
                split[i] = s[0] + ";" + s[1] + ";" + s[2] + ";" + s[3] + ";" + s[4] + ";" + s[5] + ";" + s[6] + ";" + s[7]  + "\n";
            }
        }
        for (int i = 0; i < split.length; i++) {
            finalStr += split[i] + "\n";
        }
        DataBase.getSingleTone().getController("Post").writeFile(finalStr);
        return "valid";
    }
}
