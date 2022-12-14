import styled from 'styled-components';

const UserNickname = ({ nickname }) => {
	return <Nickname>{nickname}</Nickname>;
};

export default UserNickname;

const Nickname = styled.span`
	color: #232629;
	margin: 4px;
	font-size: 34px;
	font-weight: 400;
`;
