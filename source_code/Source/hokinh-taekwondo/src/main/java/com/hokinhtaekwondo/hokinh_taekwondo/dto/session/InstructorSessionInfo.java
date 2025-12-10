package com.hokinhtaekwondo.hokinh_taekwondo.dto.session;

import com.hokinhtaekwondo.hokinh_taekwondo.model.Session;
import com.hokinhtaekwondo.hokinh_taekwondo.model.SessionUser;

public record InstructorSessionInfo(Session session, SessionUser sessionUser) {}
