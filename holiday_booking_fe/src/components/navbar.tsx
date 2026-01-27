import { NavigationMenu, NavigationMenuContent, NavigationMenuItem, NavigationMenuLink, NavigationMenuList, NavigationMenuTrigger } from "@radix-ui/react-navigation-menu"
import { Button } from "./ui/button"
import { Search, UserPlus } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "./ui/card"
import { Input } from "./ui/input"

const Navbar = () => {
    return (
        <div className="">
            <NavigationMenu className="w-full px-4 py-4 bg-white shadow-md rounded-b-xl">
                <NavigationMenuList className="flex justify-between">
                    <NavigationMenuItem>
                        <NavigationMenuTrigger className="text-2xl font-bold">Holiday Booking</NavigationMenuTrigger>
                        <NavigationMenuContent>
                            <NavigationMenuLink ></NavigationMenuLink>
                        </NavigationMenuContent>
                    </NavigationMenuItem>
                    <NavigationMenuItem className="relative">
                        <NavigationMenuTrigger asChild>
                            <Button variant="outline" className="rounded-full border-gray-200 w-10 h-10">
                                <UserPlus/>
                            </Button>
                        </NavigationMenuTrigger>
                        <NavigationMenuContent className="absolute right-0 top-full mt-2 z-50">
                            <NavigationMenuLink>
                                <Card className="w-64 shadow-lg">
                                    <CardHeader>
                                        <CardTitle>Account</CardTitle>
                                        <CardContent className="text-muted-foreground space-y-4">
                                            <p>Add a new User</p>
                                            <p>Add a new Host</p>
                                            <div className="">
                                                <p>Are you a Host? Insert your Host Code.</p>
                                                <div className="flex gap-1">
                                                    <Input type="number" max={5} placeholder="Host Code"></Input>
                                                    <Button> <Search/> </Button>
                                                </div>
                                            </div>
                                        </CardContent>
                                    </CardHeader>
                                </Card>
                            </NavigationMenuLink>
                        </NavigationMenuContent>
                    </NavigationMenuItem>
                </NavigationMenuList>
            </NavigationMenu>
        </div>
    )
}
export default Navbar