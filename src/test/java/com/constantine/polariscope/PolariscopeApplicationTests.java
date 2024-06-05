package com.constantine.polariscope.API;

import com.constantine.polariscope.DTO.MemberListItem;
import com.constantine.polariscope.DTO.NewMemberForm;
import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Service.MemberService;
import com.constantine.polariscope.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InterpersonalAPI.class)
public class PolariscopeApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MemberService memberService;

	@MockBean
	private UserService userService;

	private User perfectUser, perfectUser2, perfectObserver;
	private Member perfectMember, minimalMember;
	private NewMemberForm perfectNewMemberForm;

	LocalDate specificDate = LocalDate.of(2024, 6, 3);
	LocalDateTime specificDateTime = LocalDateTime.of(2024, 6, 3, 0, 0);

	@BeforeEach
	public void setup() {
		perfectUser = new User(UUID.randomUUID(), "test", "realPassword", User.Role.Owner, true, specificDateTime, null, null);
		perfectUser2 = new User(UUID.randomUUID(), "chuck", "realPassword", User.Role.Owner, true, specificDateTime, null, null);
		perfectObserver = new User(UUID.randomUUID(), "ak", "realPassword", User.Role.Observer, true, specificDateTime, null, null);

		perfectMember = new Member(UUID.randomUUID(), "Terence", "Bird", "Angry", Member.RelationshipType.FRIEND, Member.Sexuality.HETEROSEXUAL, "INSP", "Dark Red", "Best bird", Member.Sex.MALE, 2001, specificDate, specificDateTime, specificDateTime, perfectUser, null, null);
		minimalMember = new Member(UUID.randomUUID(), "Jim", "Bird", "Angry", null, null, null, null, null, null, null, null, specificDateTime, specificDateTime, perfectObserver, null, null);

		perfectNewMemberForm = new NewMemberForm(2001, specificDate, "Red", "Birdie", "Bird", "Angry", "ISTJ", "Costi is really cool", Member.RelationshipType.FRIEND.name(), Member.Sex.MALE.name(), Member.Sexuality.HETEROSEXUAL.name());
	}

	@Test
	@WithMockUser(username = "test")
	public void testNewMember() throws Exception {
		Mockito.when(userService.loadUserByUsername("test")).thenReturn(perfectUser);

		String jsonContent = "{" +
				"\"ageMet\": 2001," +
				"\"birthday\": \"" + specificDate + "\"," +
				"\"favoriteColor\": \"Red\"," +
				"\"firstName\": \"Birdie\"," +
				"\"lastName\": \"Bird\"," +
				"\"middleName\": \"Angry\"," +
				"\"personality\": \"ISTJ\"," +
				"\"description\": \"Costi is really cool\"," +
				"\"relationship\": \"FRIEND\"," +
				"\"sex\": \"MALE\"," +
				"\"sexuality\": \"HETEROSEXUAL\"" +
				"}";

		mockMvc.perform(post("/api/interpersonal/member/new")
						.contentType(MediaType.APPLICATION_JSON)
						.with(csrf().asHeader())
						.content(jsonContent))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Member saved to Polariscope"));

		Mockito.verify(memberService, Mockito.times(1)).save(Mockito.any(Member.class));
	}

	@Test
	@WithMockUser(username = "ak")
	public void testNewMemberObserverRestriction() throws Exception {
		Mockito.when(userService.loadUserByUsername("ak")).thenReturn(perfectObserver);

		String jsonContent = "{" +
				"\"ageMet\": 2001," +
				"\"birthday\": \"" + specificDate + "\"," +
				"\"favoriteColor\": \"Red\"," +
				"\"firstName\": \"Birdie\"," +
				"\"lastName\": \"Bird\"," +
				"\"middleName\": \"Angry\"," +
				"\"personality\": \"ISTJ\"," +
				"\"description\": \"Costi is really cool\"," +
				"\"relationship\": \"FRIEND\"," +
				"\"sex\": \"MALE\"," +
				"\"sexuality\": \"HETEROSEXUAL\"" +
				"}";

		mockMvc.perform(post("/api/interpersonal/member/new")
						.contentType(MediaType.APPLICATION_JSON)
						.with(csrf().asHeader())
						.content(jsonContent))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Error saving member"));

		Mockito.verify(memberService, Mockito.times(0)).save(Mockito.any(Member.class));
	}

	@Test
	@WithMockUser(username = "test")
	public void testAllMembers() throws Exception {
		List<MemberListItem> members = new ArrayList<>();
		members.add(new MemberListItem(perfectMember));
		Mockito.when(userService.loadUserByUsername("test")).thenReturn(perfectUser);
		Mockito.when(memberService.allMembers(perfectUser)).thenReturn(members);

		mockMvc.perform(get("/api/interpersonal/member/all"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(perfectMember.getId().toString()));

		Mockito.verify(memberService, Mockito.times(1)).allMembers(perfectUser);
	}

	@Test
	@WithMockUser(username = "test")
	public void testViewMember() throws Exception {
		Mockito.when(userService.loadUserByUsername("test")).thenReturn(perfectUser);
		Mockito.when(memberService.findMember(perfectMember.getId())).thenReturn(perfectMember);

		mockMvc.perform(get("/api/interpersonal/member/view/" + perfectMember.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(perfectMember.getId().toString()));

		Mockito.verify(memberService, Mockito.times(1)).findMember(perfectMember.getId());
	}

	@Test
	@WithMockUser(username = "ak")
	public void testViewMemberObserverRestriction() throws Exception {
		Mockito.when(userService.loadUserByUsername("ak")).thenReturn(perfectObserver);
		Mockito.when(memberService.findMember(perfectMember.getId())).thenReturn(perfectMember);

		mockMvc.perform(get("/api/interpersonal/member/view/" + perfectMember.getId()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid Account"));

		Mockito.verify(memberService, Mockito.times(0)).findMember(perfectMember.getId());
	}

	@Test
	@WithMockUser(username = "chuck")
	public void testViewMemberWrongUser() throws Exception {
		Mockito.when(userService.loadUserByUsername("chuck")).thenReturn(perfectUser2);
		Mockito.when(memberService.findMember(perfectMember.getId())).thenReturn(perfectMember);

		mockMvc.perform(get("/api/interpersonal/member/view/" + perfectMember.getId()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid Account"));

		Mockito.verify(memberService, Mockito.times(1)).findMember(perfectMember.getId());
	}

	@Test
	@WithMockUser(username = "test")
	public void testViewMemberNotFound() throws Exception {
		Mockito.when(userService.loadUserByUsername("test")).thenReturn(perfectUser);
		Mockito.when(memberService.findMember(perfectMember.getId())).thenThrow(new RuntimeException("Member not found"));

		mockMvc.perform(get("/api/interpersonal/member/view/" + perfectMember.getId()))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Member not found"));

		Mockito.verify(memberService, Mockito.times(1)).findMember(perfectMember.getId());
	}
}
