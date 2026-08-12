package com.blueberryjoy.novel.message.template;

import com.blueberryjoy.novel.user.User;

public class UpdateRoleRequestMessageDTO {
    Long userId = null;
    String username = null;

    public UpdateRoleRequestMessageDTO(User user) {
        if (user != null) {
            userId = user.getId();
            username = user.getUsername();
        }
    }
}
