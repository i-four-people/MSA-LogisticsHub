package com.logistcshub.hub.hub.presentation.request;

import java.util.List;
import java.util.UUID;

public record HubIdsDto (
        List<UUID> idList
){
}
