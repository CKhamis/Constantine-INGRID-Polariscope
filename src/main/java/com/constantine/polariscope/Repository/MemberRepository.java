package com.constantine.polariscope.Repository;

import com.constantine.polariscope.Model.MemberGroup;
import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    List<Member> findAllByAuthorOrderByLastModifiedDesc(User author);
    List<Member> findAllByGroupOrderByLastModifiedDesc(MemberGroup group);
}
