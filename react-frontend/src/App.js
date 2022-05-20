import React, {Component} from "react";
import {View} from "react-native";
import { Link, withRouter } from "react-router-dom";
import { Route, Switch } from 'react-router-dom';
import "bootstrap/dist/css/bootstrap.min.css";

import './App.css';

import AuthService from "./services/auth.service";
import Login from "./components/login.component";
import Register from "./components/register.component";
import Home from "./components/home.component";
import CreateSelfTaughtCourse from "./components/create-self-taught-course.component";
import EnrolledCourses from "./components/enrolled-courses.component";
import StudiedCourse from "./components/studied-course.component"
import CreateLesson from "./components/create_lesson";
import StudiedLesson from "./components/studied-lesson.component";
import WordQuestion from "./components/word_question.component";
import CreateSupervisedCourse from "./components/create-supervised-course.component";
import SupervisedCourses from "./components/supervised-courses.component"
import SupervisedCourse from "./components/supervised-course.component";
import SupervisedLesson from "./components/supervised-lesson.component";
import SupervisedCourseStatistics from "./components/supervised-course-statistics.component";

class App extends Component {
  constructor(props) {
    super(props);
    this.logOut = this.logOut.bind(this);
    this.state = {
      showRestaurantOwnerAdminBoard: false,
      showNewAdminBoard: false,
      showCustomerBoard: false,
      currentUser: undefined,
    };
  }

  componentDidMount() {
    document.title = "Polyglot"
    const user = AuthService.getCurrentUser();
    if (user) {
      this.setState({
        currentUser: user,
        showStudentBoard: user.role === "STUDENT",
        showTeacherBoard: user.role === "TEACHER",
      });
    }
  }

  logOut() {
    AuthService.logout();
  }

  render() {
    const { currentUser, showStudentBoard, showTeacherBoard} = this.state;
    return (
        <div>
          <nav className="navbar navbar-expand navbar-dark bg-dark">
            <Link to={"/home"} className="navbar-brand">
              <View style={{flexDirection: 'row', justifyContent: 'flex-end'}}>
                <img
                    src={require('./assets/languages.png')}
                    alt="world"
                    style={{margin: "0px 5px"}}
                />
                Polyglot
              </View>
            </Link>
            <div className="navbar-nav mr-auto">
              {showStudentBoard && (
                  <li className="nav-item">
                    <Link to={"/enrolled_courses"} className="nav-link">
                      Enrolled Courses
                    </Link>
                  </li>
              )}
              {showStudentBoard && (
                  <li className="nav-item">
                    <Link to={"/create_self_taught_course"} className="nav-link">
                      Create new Course
                    </Link>
                  </li>
              )}
              {showTeacherBoard && (
                  <li className="nav-item">
                    <Link to={"/supervised_courses"} className="nav-link">
                      Supervised Courses
                    </Link>
                  </li>
              )}
              {showTeacherBoard && (
                  <li className="nav-item">
                    <Link to={"/create_supervised_course"} className="nav-link">
                      Create new Course
                    </Link>
                  </li>
              )}
            </div>
            {currentUser ? (
                <div className="navbar-nav ml-auto">
                  <li className="nav-item">
                    <a href="/login" className="nav-link" onClick={this.logOut}>
                      LogOut
                    </a>
                  </li>
                </div>
            ) : (
                <div className="navbar-nav ml-auto">
                  <li className="nav-item">
                    <Link to={"/login"} className="nav-link">
                      Login
                    </Link>
                  </li>
                  <li className="nav-item">
                    <Link to={"/register"} className="nav-link">
                      Sign Up
                    </Link>
                  </li>
                </div>
            )}
          </nav>
          <div className="container mt-3">
            <Switch>
              <Route exact path="/home" component={Home} />
              <Route exact path="/login"  component={Login} />
              <Route exact path="/register"  component={Register} />
              <Route path="/students_home"  component={Home} />
              <Route path="/create_self_taught_course"  component={CreateSelfTaughtCourse} />
              <Route path="/enrolled_courses"  component={EnrolledCourses} />
              <Route path="/teachers_home"  component={Home} />
              <Route exact path="/course/:course_id"  component={StudiedCourse} />
              <Route exact path="/create_lesson/:course_id/:is_teacher"  component={CreateLesson} />
              <Route exact path="/lesson/:lesson_id"  component={StudiedLesson} />
              <Route exact path="/word_question/:lesson_id"  component={WordQuestion} />
              <Route exact path="/create_supervised_course"  component={CreateSupervisedCourse} />
              <Route exact path="/supervised_courses"  component={SupervisedCourses} />
              <Route exaxt path="/supervised_course/:course_id" component={SupervisedCourse}/>
              <Route exact path="/supervised_lesson/:lesson_id"  component={SupervisedLesson} />
              <Route exact path="/supervised_course_statistics/:course_id"  component={SupervisedCourseStatistics} />
            </Switch>
          </div>
        </div>
    );
  }
}
export default withRouter(App);
