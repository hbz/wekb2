<%@ page import="java.time.Year;" %>
<footer class="ui fixed footer segment wekb-footer" role="contentinfo">
    <div class="ui center aligned container">

        <div class="ui stackable  divided equal height stackable grid center aligned">

            <div class="five wide column left aligned">
                <img height="30" alt="Logo wekb" src="${resource(dir: 'images', file: 'wekb-logo.svg')}"/>

                <p>©${Year.now().getValue()} Hochschulbibliothekszentrum des Landes Nordrhein-Westfalen (hbz) <br> ‧ Jülicher Straße 6 ‧ 50674 Köln<br>  ‧ +49 221 400 75-0
                </p>
                <a rel="license" href="http://creativecommons.org/publicdomain/zero/1.0/">
                    <img alt="CC0 1.0 Universal - Public Domain Dedication"
                         src="${resource(dir: 'images', file: 'CC-Zero-badge.svg.png')}"/>
                </a>
            </div>

            <div class="three wide column left aligned">

                <h3 class="ui  header">About we:kb</h3>

                <div class="ui  link list">
                    <div class="item">
                        <a target="_blank" class="content"
                           href="https://service-wiki.hbz-nrw.de/display/WEKB/About+we%3Akb">About we:kb</a>
                    </div>

                    <div class="item">
                        <a target="_blank" class="content"
                           href="https://service-wiki.hbz-nrw.de/pages/viewpage.action?pageId=771129406">Manual  we:kb</a>
                    </div>
                </div>

                <h3 class="ui  header">Technical Details</h3>

                <div class="ui  link list">
                    <%-- App version --%>
                    <div class="item">
                       Version: ${grailsApplication.metadata['info.app.version']}
                    </div>
                    <g:if test="${grailsApplication.metadata['build.git.branch']}">
                    <%-- Git branch --%>
                        <div class="item">
                            <a target="_blank" class="content"
                               href="https://github.com/hbz/wekb2/tree/${grailsApplication.metadata['git.branch']}">
                                Branch: ${grailsApplication.metadata['build.git.branch']}
                            </a>
                        </div>
                    </g:if>
                    <g:if test="${grailsApplication.metadata['build.git.revision']}">
                    <%-- Git Commit --%>
                        <div class="item">
                            <a target="_blank" class="content"
                               href="https://github.com/hbz/wekb2/tree/${grailsApplication.metadata['build.git.revision']}">
                                Git Commit: ${grailsApplication.metadata['build.git.revision']}
                            </a>
                        </div>
                    </g:if>
                    <g:if test="${grailsApplication.metadata['build.time']}">
                    <%-- Timestamp --%>
                        <div class="item">
                            Build: ${grailsApplication.metadata['build.time']}
                        </div>
                    </g:if>
                </div>
            </div>

            <div class="four wide column left aligned">
                <h3 class="ui  header">Contact Us</h3>

                <div class="ui  link list ">
                    <div class="item"><a class="content" href="mailto:laser@hbz-nrw.de">E-Mail</a></div>

                    <div class="item"><a target="_blank" class="content"
                                         href="https://www.hbz-nrw.de/ueber-uns/kontakt/anreise">How to reach us</a>
                    </div>

                    <div class="item"><a target="_blank" class="content"
                                         href="https://www.hbz-nrw.de/impressum">Legal Notice</a></div>

                    <div class="item"><a target="_blank" class="content"
                                         href="https://www.hbz-nrw.de/datenschutz">Data Privacy Statement</a></div>
                </div>
            </div>

            <div class="four wide column left aligned">
                <h3 class="ui  header">Accessibility</h3>

                <div class="ui  link list ">
                    <div class="item"><a target="_blank" class="content"
                                         href="https://www.hbz-nrw.de/barrierefreiheit">Accessibility Statement</a>
                    </div>

                    <div class="item"><g:link controller="public" action="wcagPlainEnglish">Plain English</g:link></div>

                    <div class="item"><g:link controller="public"
                                              action="wcagFeedbackForm">Accessibility Feedback Form</g:link></div>
                </div>
            </div>
        </div>
    </div>

</footer>
