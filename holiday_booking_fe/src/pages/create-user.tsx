import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Field, FieldGroup, FieldLabel } from "@/components/ui/field";
import { Input } from "@/components/ui/input";



export function CreateUser() {

    return (
        <div className=" w-full flex flex-col h-screen  justify-center items-center">
            <Card className="w-full max-w-4xl">
                <CardHeader>
                    <CardTitle>Create a new User</CardTitle>
                    <CardDescription>
                        Enter your information below to create your account
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <form>
                        <FieldGroup >
                            <div className="grid grid-cols-2 gap-4">
                                <Field>
                                    <FieldLabel htmlFor="name">Name</FieldLabel>
                                    <Input id="name" type="text" placeholder="John" required />
                                </Field>
                                <Field>
                                    <FieldLabel htmlFor="lastName">Last Name</FieldLabel>
                                    <Input id="lastName" type="text" placeholder="Doe" required />
                                </Field>
                                <Field>
                                    <FieldLabel htmlFor="email">Email</FieldLabel>
                                    <Input
                                        id="email"
                                        type="email"
                                        placeholder="m@example.com"
                                        required
                                    />  
                                </Field>
                                <Field>
                                    <FieldLabel htmlFor="address">Residential Address</FieldLabel>
                                    <Input id="address" type="text" placeholder="Via Roma, 56" required />
                                </Field>
                                <Field>
                                    <FieldLabel htmlFor="city">City</FieldLabel>
                                    <Input id="city" type="text" placeholder="Firenze" required />
                                </Field>
                                <Field>
                                    <FieldLabel htmlFor="country">Country</FieldLabel>
                                    <Input id="country" type="text" placeholder="Italy" required />
                                </Field>
                            </div>
                            <Field>
                                <FieldLabel htmlFor="isHost">User type</FieldLabel>
    
                                <div className="flex items-center gap-2">
                                    <input
                                        id="isHost"
                                        type="checkbox"
                                        name="isHost"
                                        className="h-4 w-4"
                                    />
                                    <label htmlFor="isHost" className="text-sm underline">
                                        This user is also a Host
                                    </label>
                                </div>
                            </Field>
                            <FieldGroup>
                                <Field>
                                    <Button type="submit">Create Account</Button>
                                </Field>
                            </FieldGroup>
                        </FieldGroup>
                    </form>
                </CardContent>
            </Card>
        </div>
    );
}