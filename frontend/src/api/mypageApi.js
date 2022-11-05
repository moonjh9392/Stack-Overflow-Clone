import axios from 'axios';
import { root } from './root';

const axiosConfig = {
	baseURL: root,
};

const instance = axios.create(axiosConfig);

export const getMypageUserInfo = async () => {
	try {
		const result = await instance.get(`/user/info`);
		return result.data;
	} catch (err) {
		console.log(err);
	}
};

export const getMypageUserAnswer = async () => {
	try {
		const result = await instance.get(`/user/answers`);
		return result.data;
	} catch (err) {
		console.log(err);
	}
};

export const getMypageUserQuestion = async () => {
	try {
		const result = await instance.get(`/user/questions`);
		return result.data;
	} catch (err) {
		console.log(err);
	}
};

export const getMypageUserTag = async () => {
	try {
		const result = await instance.get(`/user/tags`);
		return result.data;
	} catch (err) {
		console.log(err);
	}
};

export const getMypageUserBookmark = async () => {
	try {
		const result = await instance.get(`/user/bookmarks`);
		return result.data;
	} catch (err) {
		console.log(err);
	}
};
