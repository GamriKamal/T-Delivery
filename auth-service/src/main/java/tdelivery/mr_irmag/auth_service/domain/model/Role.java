package tdelivery.mr_irmag.auth_service.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Роли, доступные для пользователей в системе.")
public enum Role {
    @Schema(description = "Роль обычного пользователя.", example = "USER")
    USER,

    @Schema(description = "Роль курьера, отвечающего за доставку.", example = "COURIER")
    COURIER,

    @Schema(description = "Роль администратора с расширенными правами.", example = "ADMIN")
    ADMIN
}

