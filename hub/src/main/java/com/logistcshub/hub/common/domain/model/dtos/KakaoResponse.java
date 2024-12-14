package com.logistcshub.hub.common.domain.model.dtos;

import java.util.List;

public record KakaoResponse(
        String trans_id,
        List<Route> routes
) {
    public record Route(
            int result_code,
            String result_msg,
            Summary summary,
            List<Section> sections
    ) {
        public record Summary(
                Origin origin,
                Destination destination,
                List<Waypoint> waypoints,
                String priority,
                Bound bound,
                Fare fare,
                int distance,
                int duration
        ) {
            public record Origin(
                    String name,
                    double x,
                    double y
            ) {}

            public record Destination(
                    String name,
                    double x,
                    double y
            ) {}

            public record Waypoint(
                    String name,
                    double x,
                    double y
            ) {}

            public record Bound(
                    double min_x,
                    double min_y,
                    double max_x,
                    double max_y
            ) {}

            public record Fare(
                    int taxi,
                    int toll
            ) {}
        }

        public record Section(
                int distance,
                int duration
        ) {}
    }
}

