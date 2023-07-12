# Мобильное приложение на Android для просмотра погоды
SkyCast – это простое Android-приложение, написанное на языке Kotlin, для проверки прогноза погоды.
## Возможности
*	Получение погоды для любого города или по текущему местоположению
*	Получение прогноза на несколько дней вперед
*	Автоматическое определение местоположения, если разрешено пользователем
*	Локализация на русский и английский язык
*	Приложение обращается к API для получения данных о погоде
## Используемые технологии
*	Volley – работа с сетью
*	SwipeRefreshLayout – поддержка обновления содержимого контент страницы через жест swipe-to-refresh
*	Picasso – библиотека для загрузки изображений
*	Play-services-location – библиотека для работы с местоположением, которая позволяет получать данные о местоположении по GPS
*	Для верстки UI использован xml
## Скриншот
![photo_5291805652257196970_y](https://github.com/anmischenko/SkyCast/assets/121116339/c23ac860-133f-4b76-948c-78998a1ea5d3)
## Использование
Для использования этого приложения необходимо зарегистрироваться на сайте https://www.weatherapi.com/ и получить ключ API. Затем необходимо вставить этот ключ в соответствующую переменную API_KEY в файле MainFragment.kt.
Далее, пользователь может вводить название города или разрешить приложению использовать его текущее местоположение. Приложение отправляет запрос на сервер и отображает полученную информацию о погоде.
