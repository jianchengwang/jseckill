import { http } from "../utils/http";


export const userPage = (params?: object) => {
  return http.request("get", `/operate/user/page`, { params });
};