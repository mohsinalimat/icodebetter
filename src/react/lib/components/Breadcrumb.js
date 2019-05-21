import React, { createElement } from "react";

export function findRouteName(url) {
  return routes[url];
}

export const getPaths = pathname => {
  const paths = ["/"];
  if (pathname === "/") return paths;
  pathname.split("/").reduce((prev, curr, index) => {
    const currPath = prev + "/" + curr;
    paths.push(currPath);
    return currPath;
  });
  return paths;
};

export const BreadcrumbsItem = ({ match }) => {
  var routeName = findRouteName(match.url);
  if (routeName) {
    //	  if(routeName=='Home')routeName=createElement('i',{className:'icon-home'});
    if (routeName == "Home")
      routeName = createElement(
        Button,
        {
          className: "btn-round-shadow",
          color: "home",
          style: { marginTop: "-8px" }
        },
        createElement("i", { className: "icon-home" })
      );
    return match.isExact
      ? createElement(BreadcrumbItem, { active: true }, routeName)
      : createElement(
          BreadcrumbItem,
          null,
          createElement(Link, { to: match.url || "" }, routeName)
        );
  }
  return null;
};

export const Breadcrumbs = ({ location: { pathname }, match }) => {
  const paths = getPaths(pathname);
  const items = paths.map((path, i) =>
    createElement(Route, { key: i++, path: path, component: BreadcrumbsItem })
  );
  return createElement(
    Breadcrumb,
    null,
    createElement("h3", { id: "id-breed" }, _scd.projectName || "iCodeBetter"),
    createElement("span", { style: { width: "20px" } }, " "),
    items
  );
};

export const Breadcrumb2 = props => {
  return createElement(
    "div",
    { className: "d-md-down-none" },
    createElement(
      Route,
      Object.assign({ path: "/:path", component: Breadcrumbs }, props)
    )
  );
};
/*
export default props => (
  <div>
    <Route path="/:path" component={Breadcrumbs} {...props} />
  </div>
);
*/
