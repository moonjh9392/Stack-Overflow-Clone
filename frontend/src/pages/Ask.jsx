import styled from 'styled-components';
import { useState } from 'react';
import Editor from '../components/Question/Editor';
import Button from '../components/common/Button';

const Container = styled.section`
	background-color: #f8f9f9;
	padding: 3rem 0 4.5rem 4.5rem;
`;
const Formarea = styled.form``;
const AskQuestion = styled.h1`
	font-weight: 700;
	font-size: 1.75rem;
	margin-bottom: 4rem;
`;
const WritingTip = styled.div`
	background-color: #edf4fa;
	border: 1px solid #aecdea;
	border-radius: 2px;
	line-height: 115%;
	padding: 1.75rem;
	margin-bottom: 1.5rem;
	h2 {
		font-size: 1.3rem;
		margin-bottom: 1rem;
	}
	.writingtip {
		margin-bottom: 1rem;
	}
	.steps {
		font-size: 0.9rem;
		margin-bottom: 0.9rem;
		font-weight: 700;
	}
	ul li {
		font-size: 0.85rem;
		list-style-type: disc;
		margin-left: 1rem;
	}
`;
const Group = styled.div`
	background-color: white;
	border: 1px solid #dde0e1;
	padding: 1.75rem;
	margin-bottom: 1.5rem;
`;
const SectionTitle = styled.h3`
	font-size: 1rem;
	font-weight: 700;
	line-height: 115%;
	margin-bottom: 0.3rem;
`;
const Caption = styled.div`
	font-size: 0.85rem;
	margin-bottom: 0.3rem;
`;
const Tip = styled.div``;
const Form = styled.input`
	font-size: 0.85rem;
	font-weight: 700;
	border: 1px solid rgb(179, 183, 188);
	padding-left: 0.5rem;
	height: 30px;
	width: 99%;
	border-radius: 3px;
	background-color: white;
	display: flex;
	align-items: center;
	justify-content: center;

	&:focus-within {
		outline: none;
		border-color: #9ecaed;
		box-shadow: 0 0 10px #9ecaed;
	}
	&:focus {
		outline: none;
	}
`;
// const DiscardDraft = styled.button``;

const Ask = () => {
	return (
		<Container>
			<Formarea>
				<AskQuestion>Ask a public question</AskQuestion>
				<WritingTip>
					<h2>Writing a good question</h2>
					<p className="writingtip">
						You’re ready to ask a programming-related question and this form
						will help guide you through the process. <br />
						Looking to ask a non-programming question? See the topics here to
						find a relevant site.
					</p>
					<p className="steps">Steps</p>
					<ul>
						<li>Summarize your problem in a one-line title.</li>
						<li>Describe your problem in more detail.</li>
						<li>Describe what you tried and what you expected to happen.</li>
						<li>
							Add “tags” which help surface your question to members of the
							community.
						</li>
						<li>Review your question and post it to the site.</li>
					</ul>
				</WritingTip>
				<Group>
					<SectionTitle>
						<label htmlFor="title">Title</label>
					</SectionTitle>
					<Caption>
						<label htmlFor="title">
							Be specific and imagine you’re asking a question to another
							person.
						</label>
					</Caption>
					<Form
						id="title"
						name="title"
						type="text"
						maxlength="300"
						placeholder="e.g. Is there an R function for finding the index of an element in a vector?"
					/>
				</Group>
				<Tip></Tip>
				<Group>
					<SectionTitle>What are the details of your problem?</SectionTitle>
					<Caption>
						Introduce the problem and expand on what you put in the title.
						Minimum 20 characters.
					</Caption>
					<Editor />
				</Group>
				<Tip></Tip>
				<Group>
					<SectionTitle>
						What did you try and what were you expecting?
					</SectionTitle>
					<Caption>
						Describe what you tried, what you expected to happen, and what
						actually resulted. Minimum 20 characters.
					</Caption>
					<Editor />
				</Group>
				<Tip></Tip>
				<Group>
					<SectionTitle>Tags</SectionTitle>
					<Caption>
						Add up to 5 tags to describe what your question is about. Start
						typing to see suggestions.
					</Caption>
					<Form text="tags input (beta)"></Form>
				</Group>
				<Button text="Review your question" />
			</Formarea>
		</Container>
	);
};

export default Ask;
