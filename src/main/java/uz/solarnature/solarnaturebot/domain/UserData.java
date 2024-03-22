package uz.solarnature.solarnaturebot.domain;

import lombok.Data;
import uz.solarnature.solarnaturebot.domain.enumeration.UserState;

@Data
public class UserData {

    private UserState state;
    private Long docId;

    public static UserData of(UserState state, Long docId) {
        var data = UserData.of(state);
        data.setDocId(docId);
        return data;
    }

    public static UserData of(UserState state) {
        var data = new UserData();
        data.setState(state);
        return data;
    }

}
