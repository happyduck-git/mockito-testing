package com.codestates.homework;

import com.codestates.member.dto.MemberDto;
import com.codestates.member.entity.Member;
import com.codestates.member.mapper.MemberMapper;
import com.codestates.member.service.MemberService;
import com.codestates.stamp.Stamp;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerHomeworkTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockBean
    private MemberService memberService;
    @MockBean
    private MemberMapper memberMapper;

    @Test
    void postMemberTest() throws Exception {
        // TODO MemberController의 postMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        //given
        MemberDto.Post post = new MemberDto.Post(
                "gg@gmail.com",
                "SHINGAGYOUNG",
                "010-1111-1111"
        );
        MemberDto.response responseBody = new MemberDto.response(1L,
                "gg@gmail.com",
                "SHINGAGYOUNG",
                "010-1111-1111",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp());
        String content = gson.toJson(post);
            //stubbing
        given(memberMapper.memberPostToMember(Mockito.any(MemberDto.Post.class)))
                .willReturn(new Member());

        given(memberService.createMember(Mockito.any(Member.class)))
                .willReturn(new Member());

        given(memberMapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(responseBody);

        //when
        ResultActions actions =
                mockMvc.perform(
                        post("/v11/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        //then
        MvcResult result =
                actions.andExpect(status().isCreated())
                        .andExpect(jsonPath("$.data.email").value(post.getEmail()))
                        .andReturn();
    }

    @Test
    void patchMemberTest() throws Exception {
        // TODO MemberController의 patchMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.

        //given
        Long memberId = 1L;
        MemberDto.Patch patch = new MemberDto.Patch(memberId, "GG", "010-1111-1111", Member.MemberStatus.MEMBER_ACTIVE);
        MemberDto.response response = new MemberDto.response(memberId,
                "gg@gmail.com",
                "GG",
                "010-1111-1111",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp());
        String content = gson.toJson(patch);
            //stubbing
        given(memberMapper.memberPatchToMember(Mockito.any(MemberDto.Patch.class)))
                .willReturn(new Member());
        given(memberService.updateMember(Mockito.any(Member.class)))
                .willReturn(new Member());
        given(memberMapper.memberToMemberResponse(Mockito.any(Member.class)))
                .willReturn(response);

        //when
        ResultActions actions =
                mockMvc.perform(
                        patch("/v11/members/" + memberId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        //then
        MvcResult result =
                actions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.name").value(patch.getName()))
                        .andExpect(jsonPath("$.data.phone").value(patch.getPhone()))
                        .andExpect(jsonPath("$.data.memberStatus").value(patch.getMemberStatus().getStatus())) //.getStatus()까지 해줘야 함
                        .andReturn();

    }

    @Test
    void getMemberTest() throws Exception {
        // TODO MemberController의 getMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.

        //given
        Long memberId = 1L;
        MemberDto.response responseDto = new MemberDto.response(
                memberId,
                "gg@gmail.com",
                "HS",
                "010-1111-1111",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        );
        given(memberService.findMember(Mockito.anyLong()))
                .willReturn(new Member());
        given(memberMapper.memberToMemberResponse(Mockito.any(Member.class)))
                .willReturn(responseDto);

        //when
        ResultActions actions =
                mockMvc.perform(
                        get("/v11/members/" + memberId)
                );

        //then
        MvcResult result =
                actions
                        .andExpect(status().isOk())
                        .andReturn();
    }

    @Test
    void getMembersTest() throws Exception {
        // TODO MemberController의 getMembers() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        //given
        Member member1 = new Member("hdg@hmail.com", "HGD1", "010-1111-1111");
        member1.setStamp(new Stamp());
        Member member2 = new Member("hdg2@hmail.com", "HGD2", "010-2111-1111");
        member2.setStamp(new Stamp());

        Page<Member> pageList = new PageImpl<>(List.of(member1, member2),
        PageRequest.of(0, 10, Sort.by("memberId").descending()), 2);

        MemberDto.response response1 = new MemberDto.response(1L,
                "hdg@hmail.com",
                "HGD1",
                "010-1111-1111",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp());
        MemberDto.response response2 = new MemberDto.response(2L,
                "hdg2@hmail.com",
                "HGD2",
                "010-2111-1111",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp());
        List<MemberDto.response> responses = List.of(response1,response2);

        String page = "1";
        String size = "10";
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("page", page);
        map.add("size", size);
        given(memberService.findMembers(Mockito.anyInt(), Mockito.anyInt()))
                .willReturn(pageList);
        given(memberMapper.membersToMemberResponses(Mockito.anyList()))
                .willReturn(responses);

        //when
        ResultActions actions =
                mockMvc.perform(
                        get("/v11/members/")
                                .params(map)
                );

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());


    }

    @Test
    void deleteMemberTest() throws Exception {
        // TODO MemberController의 deleteMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.

        //given
        Long memberId = 1L;
        doNothing().when(memberService).deleteMember(memberId);

        //when
        ResultActions actions =
                mockMvc.perform(
                        delete("/v11/members/" + memberId)
                );

        //then

        actions.andExpect(status().isNoContent());


    }
}
